package com.myintag.weatherapp.data.remote.dto

data class WeatherResponseDto(
    val lat: Double,
    val lon: Double,
    val timezone: String?,
    val current: CurrentWeatherDto?,
    val daily: List<DailyForecastDto>?,
    val hourly: List<HourlyWeatherDto>?
)

data class CurrentWeatherDto(
    val dt: Long,
    val sunrise: Long?,
    val sunset: Long?,
    val temp: Double?,
    val feels_like: Double?,
    val pressure: Int?,
    val humidity: Int?,
    val dew_point: Double?,
    val uvi: Double?,
    val clouds: Int?,
    val visibility: Int?,
    val wind_speed: Double?,
    val wind_deg: Int?,
    val weather: List<WeatherConditionDto>?
)

data class HourlyWeatherDto(
    val dt: Long,
    val temp: Double?,
    val feels_like: Double?,
    val pressure: Int?,
    val humidity: Int?,
    val dew_point: Double?,
    val uvi: Double?,
    val clouds: Int?,
    val visibility: Int?,
    val wind_speed: Double?,
    val wind_deg: Int?,
    val wind_gust: Double?,
    val weather: List<WeatherConditionDto>?,
    val pop: Double?
) 