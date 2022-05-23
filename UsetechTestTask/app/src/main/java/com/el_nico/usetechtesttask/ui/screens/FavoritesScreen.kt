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
import com.el_nico.usetechtesttask.domain.entity.database.SavedCityEntity
import com.el_nico.usetechtesttask.ui.activity.MainActivity
import com.el_nico.usetechtesttask.uicomponents.ListItems

sealed class Favorites {

    companion object {

        @Composable
        fun FavoritesScreen(
            activity: MainActivity, favoriteCitiesState: State<List<SavedCityEntity>>
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
                        val cities = favoriteCitiesState.value
                        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                            items(items = cities, itemContent = {
                                ListItems.SavedCityItem(
                                    activity = activity,
                                    savedCity = it
                                )
                            })
                        }
                    }

                    Button(
                        onClick = {
                            activity.navigateToSearchScreen()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, top = 10.dp, end = 30.dp, bottom = 10.dp),
                        enabled = true,
                        border = BorderStroke(width = 1.dp, brush = SolidColor(Color.Blue))
                    ) {
                        Text(text = "Добавить город для наблюдений")
                    }
                }
            }
        }
    }
}