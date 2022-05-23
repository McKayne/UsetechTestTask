package com.el_nico.usetechtesttask.ui.activity

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.el_nico.usetechtesttask.R
import com.el_nico.usetechtesttask.domain.RecentObservationsDatabase
import com.el_nico.usetechtesttask.domain.entity.database.RecentObservationsEntity
import com.el_nico.usetechtesttask.domain.entity.database.SavedCityEntity
import com.el_nico.usetechtesttask.domain.entity.networking.ImageLinkResponse
import com.el_nico.usetechtesttask.domain.entity.networking.RecentObservationsResponse
import com.el_nico.usetechtesttask.utils.GeocodingUtil
import com.el_nico.usetechtesttask.utils.networking.ImageNetworking
import com.el_nico.usetechtesttask.utils.networking.Networking
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {

    val nowLoadingState = mutableStateOf(false)

    val backgroundLoadingState = mutableStateOf<Bitmap?>(null)

    val recentObservationsState = mutableStateOf<Collection<RecentObservationsEntity>>(emptyList())

    val citiesSearchState = mutableStateOf<List<String>>(emptyList())

    val savedCitiesState = mutableStateOf<List<SavedCityEntity>>(emptyList())

    val favoriteCitiesState = mutableStateOf<List<SavedCityEntity>>(emptyList())

    val favoritesState = mutableStateOf(false)

    //

    private lateinit var db: RecentObservationsDatabase

    private val errorStateFlow = MutableStateFlow<String?>(null)

    private val backgroundStateFlow = MutableStateFlow<Bitmap?>(null)

    private val navigateToDetailsStateFlow = MutableStateFlow(false)

    private val saveCityToListStateFlow = MutableStateFlow<Boolean?>(null)

    private val updateSavedCitiesStateFlow = MutableStateFlow(false)

    private val deleteSavedCityStateFlow = MutableStateFlow(false)

    val errorFlow: StateFlow<String?> get() = errorStateFlow

    val backgroundFlow: StateFlow<Bitmap?> get() = backgroundStateFlow

    val navigateToDetailsFlow: StateFlow<Boolean> get() = navigateToDetailsStateFlow

    val saveCityToListFlow: StateFlow<Boolean?> get() = saveCityToListStateFlow

    val updateSavedCitiesFlow: StateFlow<Boolean> get() = updateSavedCitiesStateFlow

    val deleteSavedCityFlow: StateFlow<Boolean> get() = deleteSavedCityStateFlow

    private var currentCity: String? = null

    fun setupUtils(context: Context) {
        db = Room.databaseBuilder(
            context, RecentObservationsDatabase::class.java, "recent-observations.db"
        ).build()
        initNetworking(context)
    }

    private fun initNetworking(context: Context) {
        val config = readConfigFile(context)
        Networking.init(config)
        ImageNetworking.init(config)
    }

    private fun readConfigFile(context: Context): String {
        val inputStream = context.resources.openRawResource(R.raw.config)
        return inputStream.bufferedReader().use { it.readText() }
    }

    fun updateSavedCitiesList() {
        nowLoadingState.value = true

        GlobalScope.launch {
            savedCitiesState.value = db.savedCitiesDao().savedCities()
            favoriteCitiesState.value = db.savedCitiesDao().favoriteCities()

            Handler(Looper.getMainLooper()).post {
                nowLoadingState.value = false
            }
        }
    }

    fun changeBackground(link: String) {
        //"https://upload.wikimedia.org/wikipedia/commons/f/fa/Greater_Flamingo_%28Phoenicopterus_roseus%29_%288521269688%29.jpg"
        ImageNetworking.loadBackgroundImage(link)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Bitmap> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(image: Bitmap) {
                    val width = image.width
                    val height = image.height
                    if (width <= 3000 && height <= 3000) {
                        //backgroundLoadingState.value = image
                        backgroundStateFlow.value = image
                    }
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    // saved

    fun updateBirdsSpeciesInCity(
        context: Context, city: String
    ) {
        GeocodingUtil.searchForCity(context, city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Pair<Double?, Double?>> {
                override fun onSubscribe(d: Disposable) {
                    nowLoadingState.value = true
                }

                override fun onSuccess(geoPair: Pair<Double?, Double?>) {
                    val latitude = geoPair.first
                    val longitude = geoPair.second
                    if (latitude is Double && longitude is Double
                        && !(latitude == 0.0 && longitude == 0.0)) {
                        obtainRecentObservations(
                            city, latitude, longitude
                        )
                    } else {
                        nowLoadingState.value = false
                    }
                }

                override fun onError(error: Throwable) {
                    errorStateFlow.value = error.localizedMessage
                }
            })
    }

    fun obtainRecentObservations(
        city: String, latitude: Double, longitude: Double
    ) {
        Networking.recentObservations(latitude, longitude)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Array<RecentObservationsResponse>> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(results: Array<RecentObservationsResponse>) {
                    saveResultsAndContinue(city, results)
                }

                override fun onError(error: Throwable) {
                    errorStateFlow.value = error.localizedMessage
                }
            })
    }

    fun saveResultsAndContinue(
        city: String, results: Array<RecentObservationsResponse>
    ) {
        GlobalScope.launch {
            db.recentObservationsDao().deleteByCity(city)

            for (result in results) {
                db.recentObservationsDao().insert(result.dbEntity(city))
            }

            nowLoadingState.value = false
            currentCity = city

            var bird: String? = null
            val recentObservations = db.recentObservationsDao().findByCity(city)
            if (recentObservations.isNotEmpty()) {
                val observations = recentObservations.first()
                bird = observations.scientificName
            }

            recentObservationsState.value = recentObservations
            favoritesState.value = db.savedCitiesDao().isFavorite(city)
            Handler(Looper.getMainLooper()).post {
                if (bird is String) {
                    searchForBirdBackground(bird)
                }

                navigateToDetailsStateFlow.value = true
            }
        }
    }

    private fun searchForBirdBackground(bird: String) {
        ImageNetworking.imageSearch(bird)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<ImageLinkResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(response: ImageLinkResponse) {
                    val items = response.items
                    if (items is Array) {
                        val link = items[0].link
                        if (link is String) {
                            changeBackground(link)
                        }
                    }
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    // search

    fun searchByText(context: Context, text: String) {
        GeocodingUtil.searchByText(context, text)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<List<String>> {
                override fun onSubscribe(d: Disposable) {
                    nowLoadingState.value = true
                }

                override fun onSuccess(results: List<String>) {
                    GlobalScope.launch {
                        val citiesSet = mutableSetOf<String>()

                        val similarCities = db.savedCitiesDao().similarCities("%$text%")
                        for (city in similarCities) {
                            citiesSet.add(city.name)
                        }
                        for (city in results) {
                            citiesSet.add(city)
                        }

                        Handler(Looper.getMainLooper()).post {
                            nowLoadingState.value = false
                            citiesSearchState.value = citiesSet.toList()
                        }
                    }
                }

                override fun onError(error: Throwable) {
                    errorStateFlow.value = error.localizedMessage
                }

            })
    }

    fun saveCityToList(city: String) {
        nowLoadingState.value = true

        GlobalScope.launch {
            val hasSavedCity = db.savedCitiesDao().hasSavedCityWithName(city) == 1
            if (!hasSavedCity) {
                db.savedCitiesDao().insert(SavedCityEntity(city, false))
                saveCityToListStateFlow.value = true
            } else {
                saveCityToListStateFlow.value = false
            }
        }
    }

    // details

    fun changeFavoritesStatus(
        status: Boolean
    ) {
        nowLoadingState.value = true

        GlobalScope.launch {
            val city = currentCity
            if (city is String) {
                db.savedCitiesDao().changeFavoritesStatus(city, status)
            }

            Handler(Looper.getMainLooper()).post {
                nowLoadingState.value = false
                favoritesState.value = status
                updateSavedCitiesStateFlow.value = true
            }
        }
    }

    fun deleteSavedCity() {
        nowLoadingState.value = true

        GlobalScope.launch {
            val city = currentCity
            if (city is String) {
                db.recentObservationsDao().deleteByCity(city)
                db.savedCitiesDao().deleteSavedCity(city)
            }

            Handler(Looper.getMainLooper()).post {
                deleteSavedCityStateFlow.value = true
            }
        }
    }

    fun clearState() {
        errorStateFlow.value = null
        backgroundStateFlow.value = null
        navigateToDetailsStateFlow.value = false
        saveCityToListStateFlow.value = null
        updateSavedCitiesStateFlow.value = false
        deleteSavedCityStateFlow.value = false
    }
}