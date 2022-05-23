package com.el_nico.usetechtesttask.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.el_nico.usetechtesttask.ui.activity.MainActivity
import com.el_nico.usetechtesttask.uicomponents.ListItems

sealed class Search {

    companion object {

        @Composable
        fun SearchScreen(
            activity: MainActivity, citiesSearchState: State<List<String>>
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
                    var text by rememberSaveable {
                        mutableStateOf("")
                    }

                    TextField(
                        value = text,
                        onValueChange = {
                            text = it

                            if (it.isNotEmpty()) {
                                activity.searchByText(it)
                            }
                        },
                        label = {
                            Text("Поиск города для наблюдений")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        color = Color.Transparent
                    ) {
                        val cities = citiesSearchState.value
                        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                            items(items = cities, itemContent = {
                                ListItems.CitySearchItem(
                                    activity = activity,
                                    city = it
                                )
                            })
                        }
                    }

                    Button(
                        onClick = {
                            activity.backPressed()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 50.dp, top = 10.dp, end = 50.dp, bottom = 10.dp),
                        enabled = true,
                        border = BorderStroke(width = 1.dp, brush = SolidColor(Color.Blue))
                    ) {
                        Text(text = "Отмена")
                    }
                }
            }
        }
    }
}