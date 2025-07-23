package com.myintag.weatherapp.utils

import com.myintag.weatherapp.data.remote.dto.*
import com.myintag.weatherapp.model.*

fun GeocodingDto.toDomain(defaultName: String = ""): Location =
    Location(
        name = this.name ?: defaultName,
        lat = this.lat,
        lon = this.lon,
        country = this.country,
        state = this.state
    )

fun DailyForecastDto.toDomain(): DailyForecast =
    DailyForecast(
        date = this.dt,
        summary = this.summary,
        tempDay = this.temp.day,
        tempMin = this.temp.min,
        tempMax = this.temp.max,
        icon = this.weather?.firstOrNull()?.icon,
        weatherMain = this.weather?.firstOrNull()?.main,
        weatherDescription = this.weather?.firstOrNull()?.description
    )

fun HourlyWeatherDto.toDomain(): HourlyWeather =
    HourlyWeather(
        dt = this.dt,
        temp = this.temp,
        weatherIcon = this.weather?.firstOrNull()?.icon,
        weatherMain = this.weather?.firstOrNull()?.main,
        weatherDescription = this.weather?.firstOrNull()?.description
    )

fun DailyForecastDto.toWeatherDetails(hourly: List<HourlyWeather>? = null): WeatherDetails =
    WeatherDetails(
        date = this.dt,
        sunrise = this.sunrise,
        sunset = this.sunset,
        tempDay = this.temp.day,
        tempNight = this.temp.night,
        pressure = this.pressure,
        humidity = this.humidity,
        dewPoint = this.dew_point,
        windSpeed = this.wind_speed,
        windDeg = this.wind_deg,
        clouds = this.clouds,
        uvi = this.uvi,
        visibility = null, // Not available in daily
        rain = this.rain,
        snow = this.snow,
        weatherMain = this.weather?.firstOrNull()?.main,
        weatherDescription = this.weather?.firstOrNull()?.description,
        icon = this.weather?.firstOrNull()?.icon,
        hourly = hourly,
        summary = this.summary
    )