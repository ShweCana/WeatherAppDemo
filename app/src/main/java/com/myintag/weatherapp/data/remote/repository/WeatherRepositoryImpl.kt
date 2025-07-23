package com.myintag.weatherapp.data.remote.repository

import com.myintag.weatherapp.data.remote.api.GeocodingApi
import com.myintag.weatherapp.data.remote.api.WeatherApi
import com.myintag.weatherapp.data.remote.dto.WeatherResponseDto
import com.myintag.weatherapp.model.DailyForecast
import com.myintag.weatherapp.model.Location
import com.myintag.weatherapp.model.WeatherDetails
import com.myintag.weatherapp.repository.WeatherRepository
import com.myintag.weatherapp.utils.toDomain
import com.myintag.weatherapp.utils.toWeatherDetails
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val geocodingApi: GeocodingApi,
    private val weatherApi: WeatherApi,
    private val apiKey: String
) : WeatherRepository {
    private val weatherCache by lazy {  ConcurrentHashMap<String, WeatherCacheEntry>() }
    private val cityCache by lazy {  ConcurrentHashMap<String, Location>() }
    private val cacheExpiryMillis = 2 * 60_000L // 2 minute

    override suspend fun getLocationByCity(city: String): Location? {
        return getCityLocation(city)
    }

    override suspend fun getCityByCoordinates(lat: Double, lon: Double): Location? {
        val result = geocodingApi.getCityByCoordinates(lat, lon, apiKey = apiKey)
        val dto = result.firstOrNull() ?: return null
        return dto.toDomain()
    }

    override suspend fun getWeekForecast(lat: Double, lon: Double): List<DailyForecast> {
        return getWeather(lat, lon).daily?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getWeatherDetails(lat: Double, lon: Double, dayTimestamp: Long): WeatherDetails? {
        val weatherResponse = getWeather(lat, lon)
        val day = weatherResponse.daily?.find { it.dt == dayTimestamp } ?: return null
        // Filter hourly data for the selected day
        val hourlyList = weatherResponse.hourly
            ?.filter { it.dt >= dayTimestamp && it.dt < dayTimestamp + 86400 } // 24x3600 seconds for a day
            ?.map { it.toDomain() }
        return day.toWeatherDetails(hourly = hourlyList)
    }

    private suspend fun getCityLocation(city: String): Location? {
        return cityCache[city] ?: run {
            val result = geocodingApi.getLocationByCity(city, apiKey = apiKey)
            val dto = result.firstOrNull() ?: return null
            val location = dto.toDomain(city)
            cityCache[city] = location
            location
        }
    }

    private suspend fun getWeather(lat: Double, lon: Double): WeatherResponseDto {
        val key = "$lat,$lon"
        val now = System.currentTimeMillis()
        val cacheEntry = weatherCache[key]
        return if (cacheEntry != null && now - cacheEntry.timestamp < cacheExpiryMillis) {
            cacheEntry.response
        } else {
            val freshResponse = weatherApi.getWeather(lat, lon, apiKey)
            weatherCache[key] = WeatherCacheEntry(freshResponse, now)
            freshResponse
        }
    }
}

data class WeatherCacheEntry(val response: WeatherResponseDto, val timestamp: Long)
