package com.el_nico.usetechtesttask.uicomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.el_nico.usetechtesttask.domain.entity.database.SavedCityEntity
import com.el_nico.usetechtesttask.ui.activity.MainActivity

sealed class ListItems {

    companion object {

        @Composable
        fun CitySearchItem(
            activity: MainActivity, city: String
        ) {
            Row(modifier = Modifier.fillMaxWidth().clickable {
                activity.saveCityToList(city)
            }) {
                Column {
                    Text(text = city, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        @Composable
        fun SavedCityItem(
            activity: MainActivity, savedCity: SavedCityEntity
        ) {
            Row(modifier = Modifier.fillMaxWidth().clickable {
                activity.updateBirdsSpeciesInCity(savedCity.name)
            }) {
                Column {
                    Text(text = savedCity.name, style = MaterialTheme.typography.bodyLarge)
                    Text(text = if (savedCity.isFavorite) "В избранном" else "Не в избранном",
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        @Composable
        fun RecentObservationsItem(
            recentObservations: Collection<Pair<String, String>>
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column {
                    for (recent in recentObservations) {
                        Text(text = recent.first, style = MaterialTheme.typography.bodyLarge)
                        Text(text = recent.second, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}