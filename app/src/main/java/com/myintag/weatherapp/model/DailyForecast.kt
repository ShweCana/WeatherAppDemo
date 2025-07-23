package com.myintag.weatherapp.model

data class DailyForecast(
    val date: Long,
    val summary: String?,
    val tempDay: Double?,
    val tempMin: Double?,
    val tempMax: Double?,
    val icon: String?,
    val weatherMain: String?,
    val weatherDescription: String?
) 