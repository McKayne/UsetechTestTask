package com.el_nico.usetechtesttask.domain.entity.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_observations")
data class RecentObservationsEntity(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "city") var city: String,
    @ColumnInfo(name = "species_code") var speciesCode: String,
    @ColumnInfo(name = "comName") var commonName: String,
    @ColumnInfo(name = "sci_name") var scientificName: String,
    @ColumnInfo(name = "loc_id") var locationID: String,
    @ColumnInfo(name = "loc_name") var locationName: String,
    @ColumnInfo(name = "obs_dt") var observationDate: String,
    @ColumnInfo(name = "how_many") var howMany: Int,
    @ColumnInfo(name = "lat") var latitude: Double,
    @ColumnInfo(name = "lng") var longitude: Double,
    @ColumnInfo(name = "obs_valid") var observationValid: Boolean,
    @ColumnInfo(name = "obs_reviewed") var observationReviewed: Boolean,
    @ColumnInfo(name = "location_private") var locationPrivate: Boolean,
    @ColumnInfo(name = "sub_id") var subID: String
    )