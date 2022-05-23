package com.el_nico.usetechtesttask.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.el_nico.usetechtesttask.domain.entity.database.RecentObservationsEntity

@Dao
interface RecentObservationsDao {
    @Query("SELECT * FROM recent_observations WHERE city = :city")
    fun findByCity(city: String): List<RecentObservationsEntity>

    @Insert
    fun insert(recentObservations: RecentObservationsEntity)

    @Insert
    fun insertAll(recentObservations: Collection<RecentObservationsEntity>)

    @Query("DELETE FROM recent_observations WHERE city = :city")
    fun deleteByCity(city: String)
}