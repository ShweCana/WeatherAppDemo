package com.myintag.weatherapp.di

import com.google.gson.Gson
import com.myintag.weatherapp.data.remote.api.GeocodingApi
import com.myintag.weatherapp.data.remote.api.WeatherApi
import com.myintag.weatherapp.data.remote.dto.GeocodingDto
import com.myintag.weatherapp.data.remote.dto.WeatherResponseDto
import com.myintag.weatherapp.data.remote.repository.WeatherRepositoryImpl
import com.myintag.weatherapp.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestWeatherRepositoryModule {
    private const val TORONTO_WEATHER_JSON = """
    {
      "lat": 43.6535,
      "lon": -79.3839,
      "timezone": "America/Toronto",
      "timezone_offset": -14400,
      "current": {
        "dt": 1753121532,
        "sunrise": 1753091725,
        "sunset": 1753145528,
        "temp": 22.52,
        "feels_like": 22.11,
        "pressure": 1017,
        "humidity": 49,
        "dew_point": 11.28,
        "uvi": 8.49,
        "clouds": 1,
        "visibility": 10000,
        "wind_speed": 1.79,
        "wind_deg": 170,
        "wind_gust": 3.13,
        "weather": [
          {
            "id": 800,
            "main": "Clear",
            "description": "clear sky",
            "icon": "01d"
          }
        ]
      },
      "daily": [
        {
          "dt": 1753117200,
          "sunrise": 1753091725,
          "sunset": 1753145528,
          "summary": "You can expect clear sky in the morning, with partly cloudy in the afternoon",
          "temp": {
            "day": 22.49,
            "min": 16.91,
            "max": 23.42,
            "night": 17.91,
            "eve": 23.25,
            "morn": 17.77
          },
          "pressure": 1017,
          "humidity": 48,
          "dew_point": 10.94,
          "wind_speed": 4.92,
          "wind_deg": 335,
          "weather": [
            {
              "id": 800,
              "main": "Clear",
              "description": "clear sky",
              "icon": "01d"
            }
          ],
          "clouds": 1,
          "pop": 0,
          "uvi": 8.67
        },
        {
          "dt": 1753203600,
          "sunrise": 1753178184,
          "sunset": 1753231875,
          "summary": "Expect a day of partly cloudy with clear spells",
          "temp": {
            "day": 23.36,
            "min": 14.97,
            "max": 23.58,
            "night": 20.93,
            "eve": 23.08,
            "morn": 16.07
          },
          "pressure": 1022,
          "humidity": 46,
          "dew_point": 10.51,
          "wind_speed": 4.74,
          "wind_deg": 123,
          "weather": [
            {
              "id": 800,
              "main": "Clear",
              "description": "clear sky",
              "icon": "01d"
            }
          ],
          "clouds": 3,
          "pop": 0,
          "uvi": 8.74
        },
        {
          "dt": 1753290000,
          "sunrise": 1753264643,
          "sunset": 1753318220,
          "summary": "There will be partly cloudy today",
          "temp": {
            "day": 25.41,
            "min": 19.04,
            "max": 26.95,
            "night": 23.68,
            "eve": 25.78,
            "morn": 19.53
          },
          "pressure": 1020,
          "humidity": 49,
          "dew_point": 12.98,
          "wind_speed": 4.25,
          "wind_deg": 135,
          "weather": [
            {
              "id": 803,
              "main": "Clouds",
              "description": "broken clouds",
              "icon": "04d"
            }
          ],
          "clouds": 62,
          "pop": 0,
          "uvi": 7.91
        }
      ]
    }
    """

    private const val TORONTO_GEOCODING_JSON = """
    [
      {
        "name": "Toronto",
        "lat": 43.6534817,
        "lon": -79.3839347,
        "country": "CA",
        "state": "Ontario"
      }
    ]
    """

    @Provides
    @Singleton
    fun provideGeocodingApi(): GeocodingApi {
        val gson = Gson()
        val torontoGeocodingResponse =
            gson.fromJson(TORONTO_GEOCODING_JSON, Array<GeocodingDto>::class.java).toList()

        return object : GeocodingApi {
            override suspend fun getLocationByCity(
                city: String,
                limit: Int,
                apiKey: String
            ): List<GeocodingDto> {
                return if (city.equals("Toronto", ignoreCase = true)) {
                    torontoGeocodingResponse
                } else {
                    emptyList()
                }
            }

            override suspend fun getCityByCoordinates(
                lat: Double,
                lon: Double,
                limit: Int,
                apiKey: String
            ): List<GeocodingDto> {
                return torontoGeocodingResponse
            }
        }
    }

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi {
        val gson = Gson()
        val weatherResponse = gson.fromJson(TORONTO_WEATHER_JSON, WeatherResponseDto::class.java)

        return object : WeatherApi {
            override suspend fun getWeather(
                lat: Double,
                lon: Double,
                apiKey: String,
                units: String,
                exclude: String
            ): WeatherResponseDto {
                return weatherResponse
            }
        }
    }

    @Provides
    @Singleton
    fun provideApiKey(): String = "test_api_key"

    @Provides
    @Singleton
    fun provideWeatherRepository(
        geocodingApi: GeocodingApi,
        weatherApi: WeatherApi,
        apiKey: String
    ): WeatherRepository {
        return WeatherRepositoryImpl(geocodingApi, weatherApi, apiKey)
    }
} 