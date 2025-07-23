package com.myintag.weatherapp.data.remote.dto

data class DailyForecastDto(
    val dt: Long,
    val sunrise: Long?,
    val sunset: Long?,
    val moonrise: Long?,
    val moonset: Long?,
    val moon_phase: Double?,
    val summary: String?,
    val temp: TempDto,
    val pressure: Int?,
    val humidity: Int?,
    val dew_point: Double?,
    val wind_speed: Double?,
    val wind_gust: Double?,
    val wind_deg: Int?,
    val clouds: Int?,
    val uvi: Double?,
    val pop: Double?,
    val rain: Double?,
    val snow: Double?,
    val weather: List<WeatherConditionDto>?
)

data class TempDto(
    val morn: Double?,
    val day: Double?,
    val eve: Double?,
    val night: Double?,
    val min: Double?,
    val max: Double?
)

data class WeatherConditionDto(
    val id: Int?,
    val main: String?,
    val description: String?,
    val icon: String?
) 