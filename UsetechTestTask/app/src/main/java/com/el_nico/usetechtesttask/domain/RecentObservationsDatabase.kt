package com.el_nico.usetechtesttask.domain

import androidx.room.Database
import androidx.room.RoomDatabase
import com.el_nico.usetechtesttask.domain.entity.database.RecentObservationsEntity
import com.el_nico.usetechtesttask.domain.entity.database.SavedCityEntity

@Database(entities = [
    RecentObservationsEntity::class, SavedCityEntity::class
                     ], version = 1)
abstract class RecentObservationsDatabase: RoomDatabase() {
    abstract fun recentObservationsDao(): RecentObservationsDao
    abstract fun savedCitiesDao(): SavedCitiesDao
}