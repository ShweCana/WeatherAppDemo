package com.myintag.weatherapp.data.remote.dto

data class GeocodingDto(
    val name: String?,
    val lat: Double,
    val lon: Double,
    val country: String?,
    val state: String?
) 