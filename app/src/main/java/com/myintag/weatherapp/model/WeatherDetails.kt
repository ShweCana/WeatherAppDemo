package com.myintag.weatherapp.model

data class WeatherDetails(
    val date: Long,
    val sunrise: Long?,
    val sunset: Long?,
    val tempDay: Double?,
    val tempNight: Double?,
    val pressure: Int?,
    val humidity: Int?,
    val dewPoint: Double?,
    val windSpeed: Double?,
    val windDeg: Int?,
    val clouds: Int?,
    val uvi: Double?,
    val visibility: Int?,
    val rain: Double?,
    val snow: Double?,
    val weatherMain: String?,
    val weatherDescription: String?,
    val icon: String?,
    val hourly: List<HourlyWeather>? = null,
    val summary: String? = null
)

data class HourlyWeather(
    val dt: Long,
    val temp: Double?,
    val weatherIcon: String?,
    val weatherMain: String?,
    val weatherDescription: String?
) 