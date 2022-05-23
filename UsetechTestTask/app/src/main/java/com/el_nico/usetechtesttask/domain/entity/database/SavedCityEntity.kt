package com.el_nico.usetechtesttask.domain.entity.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_cities")
data class SavedCityEntity(
    @PrimaryKey var name: String,
    @ColumnInfo(name = "is_favorite") var isFavorite: Boolean
)