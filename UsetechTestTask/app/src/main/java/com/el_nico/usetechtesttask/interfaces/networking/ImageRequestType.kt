package com.el_nico.usetechtesttask.interfaces.networking

import com.el_nico.usetechtesttask.domain.entity.networking.ImageLinkResponse
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ImageRequestType {

    @GET
    fun image(
        @Url url: String
    ): Call<ResponseBody>

    @GET("customsearch/v1")
    fun imageSearch(
        @Query("key") key: String,
        @Query("cx") cx: String,
        @Query("q") query: String,
        @Query("searchType") searchType: String
    ): Single<Response<ImageLinkResponse>>
}