package com.el_nico.usetechtesttask.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.el_nico.usetechtesttask.domain.entity.database.RecentObservationsEntity
import com.el_nico.usetechtesttask.ui.activity.MainActivity
import com.el_nico.usetechtesttask.uicomponents.ListItems

sealed class Details {

    companion object {

        @Composable
        fun DetailsScreen(
            activity: MainActivity,
            recentObservationsState: State<Collection<RecentObservationsEntity>>,
            favoritesState: State<Boolean>
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                color = Color.Transparent
            ) {
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
                            .weight(1f),
                        color = Color.Transparent
                    ) {
                        val recentObservations = ArrayList<Collection<Pair<String, String>>>()
                        for (recent in recentObservationsState.value) {
                            val observations = listOf(
                                Pair("Город:", recent.city),
                                Pair("Местность:", recent.locationName),
                                Pair("Координаты места:", "${recent.latitude} ${recent.longitude}"),
                                Pair("Обычное наименование:", recent.commonName),
                                Pair("Научное наименование:", recent.scientificName),
                                Pair("Количество птиц в стае:", recent.howMany.toString())
                            )
                            recentObservations.add(observations)
                        }
                        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                            items(items = recentObservations, itemContent = {
                                ListItems.RecentObservationsItem(
                                    recentObservations = it
                                )
                            })
                        }
                    }

                    Button(
                        onClick = {
                            activity.changeFavoritesStatus(!favoritesState.value)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, top = 10.dp, end = 30.dp, bottom = 10.dp),
                        enabled = true,
                        border = BorderStroke(width = 1.dp, brush = SolidColor(Color.Blue))
                    ) {
                        Text(text = if (favoritesState.value) "Убрать из избранного"
                        else "Добавить в избранное")
                    }

                    Button(
                        onClick = {
                            activity.deleteSavedCity()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, top = 10.dp, end = 30.dp, bottom = 10.dp),
                        enabled = true,
                        border = BorderStroke(width = 1.dp, brush = SolidColor(Color.Blue))
                    ) {
                        Text(text = "Удалить город")
                    }
                }
            }
        }
    }
}