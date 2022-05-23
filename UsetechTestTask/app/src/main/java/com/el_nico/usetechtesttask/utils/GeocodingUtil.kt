package com.el_nico.usetechtesttask.utils

import android.content.Context
import android.location.Geocoder
import io.reactivex.rxjava3.core.Single
import java.util.*
import java.util.concurrent.Executors

class GeocodingUtil {

    companion object {

        fun searchForCity(context: Context, city: String): Single<Pair<Double?, Double?>> {
            return Single.create {
                Executors.newSingleThreadExecutor().execute {
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val geoResults = geocoder.getFromLocationName(city, 1)

                        if (geoResults.isNotEmpty()) {
                            val address = geoResults[0]
                            it.onSuccess(Pair(address.latitude, address.longitude))
                        } else {
                            it.onSuccess(Pair(null, null))
                        }
                    } catch (e: Exception) {
                        it.onError(e)
                    }
                }
            }
        }

        fun searchByText(context: Context, text: String): Single<List<String>> {
            return Single.create {
                Executors.newSingleThreadExecutor().execute {
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val geoResults = geocoder.getFromLocationName(text, 10)

                        val results = ArrayList<String>()
                        for (address in geoResults) {
                            val index = address.maxAddressLineIndex
                            for (i in 0..index) {
                                val line = address.getAddressLine(i)
                                results.add(line)
                            }
                        }

                        it.onSuccess(results)
                    } catch (e: Exception) {
                        it.onError(e)
                    }
                }
            }
        }
    }
}