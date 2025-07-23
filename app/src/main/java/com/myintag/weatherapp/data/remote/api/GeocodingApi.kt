package com.myintag.weatherapp.data.remote.api

import com.myintag.weatherapp.data.remote.dto.GeocodingDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("geo/1.0/direct")
    suspend fun getLocationByCity(
        @Query("q") city: String,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String
    ): List<GeocodingDto>

    @GET("geo/1.0/reverse")
    suspend fun getCityByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String
    ): List<GeocodingDto>
} 