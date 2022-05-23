package com.el_nico.usetechtesttask.interfaces.networking

import com.el_nico.usetechtesttask.domain.entity.networking.RecentObservationsResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RequestType {

    @GET("recent")
    fun recent(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Header("X-eBirdApiToken") token: String
    ): Single<Response<Array<RecentObservationsResponse>>>

    /*@GET("forecast")
    fun forecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): Single<Response<ForecastResponse>>

    @GET("onecall")
    fun onecall(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): Single<Response<OneCallResponse>>*/
}