package com.myintag.weatherapp.repository

import com.myintag.weatherapp.model.Location
import com.myintag.weatherapp.model.DailyForecast
import com.myintag.weatherapp.model.WeatherDetails

interface WeatherRepository {
    suspend fun getLocationByCity(city: String): Location?
    suspend fun getCityByCoordinates(lat: Double, lon: Double): Location?
    suspend fun getWeekForecast(lat: Double, lon: Double): List<DailyForecast>
    suspend fun getWeatherDetails(lat: Double, lon: Double, dayTimestamp: Long): WeatherDetails?
} 