package com.el_nico.usetechtesttask.ui.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.el_nico.usetechtesttask.extensions.composable
import com.el_nico.usetechtesttask.ui.screens.Details
import com.el_nico.usetechtesttask.ui.screens.Favorites
import com.el_nico.usetechtesttask.ui.screens.SavedCities
import com.el_nico.usetechtesttask.ui.screens.Search
import com.el_nico.usetechtesttask.ui.theme.UsetechTestTaskTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity: ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setupUtils(this)

        setContent {
            UsetechTestTaskTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val bitmap = viewModel.backgroundLoadingState.value
                    val imageBitmap = if (bitmap is Bitmap)
                        bitmap.asImageBitmap() else ImageBitmap(1, 1)
                    Image(
                        modifier = Modifier.fillMaxSize().alpha(0.3f),
                        bitmap = imageBitmap,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )

                    RootContents()
                    nowLoadingIndicator()
                }
            }
        }

        setupObservers()
        updateSavedCitiesList()
    }

    @Composable
    fun nowLoadingIndicator(): ComposeView {
        return ComposeView(this).apply {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val alpha = if (viewModel.nowLoadingState.value) 1.0f else 0.0f
                CircularProgressIndicator(
                    modifier = Modifier.alpha(alpha)
                )
            }
        }
    }

    @Composable
    fun RootContents() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                color = Color.Transparent
            ) {
                navController = rememberNavController()
                NavHost(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    navController = navController,
                    startDestination = "savedcities") {
                    composable(route = "savedcities") {
                        SavedCities.SavedCitiesScreen(
                            this@MainActivity, viewModel.savedCitiesState
                        )
                    }
                    composable(route = "details") {
                        Details.DetailsScreen(
                            this@MainActivity,
                            viewModel.recentObservationsState,
                            viewModel.favoritesState
                        )
                    }
                    composable(route = "favorites") {
                        Favorites.FavoritesScreen(
                            this@MainActivity, viewModel.favoriteCitiesState
                        )
                    }
                    composable(route = "search") {
                        Search.SearchScreen(
                            this@MainActivity, viewModel.citiesSearchState
                        )
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        UsetechTestTaskTheme {
            RootContents()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.backgroundFlow.collect {
                if (it is Bitmap) {
                    viewModel.clearState()
                    viewModel.backgroundLoadingState.value = it
                }
            }
        }
        lifecycleScope.launch {
            viewModel.errorFlow.collect {
                viewModel.clearState()
                viewModel.nowLoadingState.value = false
                Toast.makeText(
                    this@MainActivity, it, Toast.LENGTH_LONG
                ).show()
            }
        }
        lifecycleScope.launch {
            viewModel.navigateToDetailsFlow.collect {
                if (it) {
                    viewModel.clearState()
                    navController.navigate("details")
                }
            }
        }
        lifecycleScope.launch {
            viewModel.saveCityToListFlow.collect {
                if (it is Boolean) {
                    viewModel.clearState()
                    if (it) {
                        updateSavedCitiesList()

                        Handler(Looper.getMainLooper()).post {
                            navController.popBackStack()
                        }
                    } else {
                        Handler(Looper.getMainLooper()).post {
                            viewModel.nowLoadingState.value = false
                            navController.popBackStack()
                            Toast.makeText(
                                this@MainActivity, "Город уже в списке", Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.updateSavedCitiesFlow.collect {
                if (it) {
                    viewModel.clearState()
                    updateSavedCitiesList()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.deleteSavedCityFlow.collect {
                if (it) {
                    viewModel.clearState()
                    navController.popBackStack()
                    updateSavedCitiesList()
                }
            }
        }
    }

    private fun updateSavedCitiesList() {
        viewModel.updateSavedCitiesList()
    }

    fun navigateToSearchScreen() {
        navController.navigate("search")
    }

    fun navigateToFavoritesScreen() {
        navController.navigate("favorites")
    }

    fun deleteSavedCity() {
        viewModel.deleteSavedCity()
    }

    fun changeFavoritesStatus(status: Boolean) {
        viewModel.changeFavoritesStatus(status)
    }

    fun saveCityToList(city: String) {
        viewModel.saveCityToList(city)
    }

    fun updateBirdsSpeciesInCity(city: String) {
        viewModel.updateBirdsSpeciesInCity(this, city)
    }

    fun searchByText(text: String) {
        viewModel.searchByText(this, text)
    }

    fun backPressed() {
        navController.popBackStack()
    }
}