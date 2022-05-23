package com.el_nico.usetechtesttask.utils.networking

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.el_nico.usetechtesttask.domain.entity.networking.ImageLinkResponse
import com.el_nico.usetechtesttask.interfaces.networking.ImageRequestType
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ImageNetworking {

    companion object {

        private const val baseURL = "https://www.googleapis.com/"

        private lateinit var requestType: ImageRequestType

        private lateinit var client: OkHttpClient

        private var apiKey: String? = null

        private var cx: String? = null

        fun init(config: String) {
            loadConfig(config)

            val rxAdapter = RxJava3CallAdapterFactory
                .createWithScheduler(Schedulers.io())

            client = OkHttpClient.Builder()
                .connectTimeout(55, TimeUnit.SECONDS)
                .readTimeout(55, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build()

            requestType = retrofit.create(ImageRequestType::class.java)
        }

        private fun loadConfig(config: String) {
            val configJSON = JSONObject(config)
            if (configJSON.has("google_api_key")) {
                apiKey = configJSON.getString("google_api_key")
            }
            if (configJSON.has("google_cx")) {
                cx = configJSON.getString("google_cx")
            }
        }

        fun imageSearch(city: String): Single<ImageLinkResponse> {
            val apiKey = apiKey
            val cx = cx
            return if (apiKey is String && cx is String) {
                return requestType.imageSearch(
                    apiKey,
                    cx,
                    city, "image").flatMap {
                    val responseBody = it.body()
                    if (responseBody is ImageLinkResponse) {
                        Single.just(responseBody)
                    } else {
                        Single.error(Throwable(it.errorBody()?.string()))
                    }
                }
            } else {
                Single.error(Throwable("Отстуствует ключ Google API"))
            }
        }

        fun loadBackgroundImage(url: String): Single<Bitmap> {
            return Single.create {
                requestType.image(url)
                    .enqueue(object: Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                val responseBody = response.body()
                                if (responseBody is ResponseBody) {
                                    val bitmap = BitmapFactory.decodeStream(responseBody.byteStream())
                                    it.onSuccess(bitmap)
                                } else {
                                    it.onError(Throwable(response.errorBody().toString()))
                                }
                            } else {
                                it.onError(Throwable(response.errorBody().toString()))
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            it.onError(t)
                        }
                    })
            }
        }
    }
}