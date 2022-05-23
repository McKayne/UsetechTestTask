package com.el_nico.usetechtesttask.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.el_nico.usetechtesttask.domain.entity.database.SavedCityEntity

@Dao
interface SavedCitiesDao {

    @Query("SELECT * FROM saved_cities")
    fun savedCities(): List<SavedCityEntity>

    @Query("SELECT * FROM saved_cities WHERE is_favorite = 1")
    fun favoriteCities(): List<SavedCityEntity>

    @Query("SELECT * FROM saved_cities WHERE name LIKE :name")
    fun similarCities(name: String): List<SavedCityEntity>

    @Query("SELECT COUNT(*) FROM saved_cities WHERE name = :name")
    fun hasSavedCityWithName(name: String): Int

    @Query("SELECT is_favorite FROM saved_cities WHERE name = :name")
    fun isFavorite(name: String): Boolean

    @Query("UPDATE saved_cities SET is_favorite = :isFavorite WHERE name = :name")
    fun changeFavoritesStatus(name: String, isFavorite: Boolean)

    @Insert
    fun insert(savedCity: SavedCityEntity)

    @Query("DELETE FROM saved_cities WHERE name = :name")
    fun deleteSavedCity(name: String)
}