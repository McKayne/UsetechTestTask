package com.el_nico.usetechtesttask.utils.networking

import com.el_nico.usetechtesttask.domain.entity.networking.RecentObservationsResponse
import com.el_nico.usetechtesttask.interfaces.networking.RequestType
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Networking {

    companion object {

        private const val baseURL = "https://api.ebird.org/v2/data/obs/geo/"

        lateinit var requestType: RequestType

        private val interceptor = HttpLoggingInterceptor()

        private lateinit var client: OkHttpClient

        internal var apiKey: String? = null

        fun init(config: String) {
            loadConfig(config)

            val rxAdapter = RxJava3CallAdapterFactory
                .createWithScheduler(Schedulers.io())

            interceptor.level = HttpLoggingInterceptor.Level.BODY
            client = OkHttpClient.Builder()
                .connectTimeout(55, TimeUnit.SECONDS)
                .readTimeout(55, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build()

            requestType = retrofit.create(RequestType::class.java)
        }

        private fun loadConfig(config: String) {
            val configJSON = JSONObject(config)
            if (configJSON.has("ebird_api_key")) {
                apiKey = configJSON.getString("ebird_api_key")
            }
        }

        ///

        fun recentObservations(
            latitude: Double, longitude: Double
        ): Single<Array<RecentObservationsResponse>> {
            val apiKey = apiKey
            return if (apiKey is String) {
                requestType.recent(latitude, longitude, apiKey).flatMap {
                    val responseBody = it.body()
                    if (responseBody is Array<RecentObservationsResponse>) {
                        Single.just(responseBody)
                    } else {
                        Single.error(Throwable(it.errorBody()?.string()))
                    }
                }
            } else {
                Single.error(Throwable("Отстуствует ключ API"))
            }
        }
    }
}