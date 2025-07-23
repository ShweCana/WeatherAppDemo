package com.myintag.weatherapp.model

data class Location(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String?,
    val state: String?
) 