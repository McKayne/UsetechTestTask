package com.el_nico.usetechtesttask.domain.entity.networking

import com.el_nico.usetechtesttask.domain.entity.database.RecentObservationsEntity
import com.google.gson.annotations.SerializedName

class RecentObservationsResponse(
    @SerializedName("speciesCode") var speciesCode: String,
    @SerializedName("comName") var commonName: String,
    @SerializedName("sciName") var scientificName: String,
    @SerializedName("locId") var locationID: String,
    @SerializedName("locName") var locationName: String,
    @SerializedName("obsDt") var observationDate: String,
    @SerializedName("howMany") var howMany: Int,
    @SerializedName("lat") var latitude: Double,
    @SerializedName("lng") var longitude: Double,
    @SerializedName("obsValid") var observationValid: Boolean,
    @SerializedName("obsReviewed") var observationReviewed: Boolean,
    @SerializedName("locationPrivate") var locationPrivate: Boolean,
    @SerializedName("subId") var subID: String
) {
    fun dbEntity(city: String): RecentObservationsEntity {
        return RecentObservationsEntity(
            0,
            city,
            speciesCode,
            commonName,
            scientificName,
            locationID,
            locationName,
            observationDate,
            howMany,
            latitude,
            longitude,
            observationValid,
            observationReviewed,
            locationPrivate,
            subID
        )
    }
}