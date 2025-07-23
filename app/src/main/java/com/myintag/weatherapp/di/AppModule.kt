package com.myintag.weatherapp.di

import com.myintag.weatherapp.BuildConfig
import com.myintag.weatherapp.data.remote.api.GeocodingApi
import com.myintag.weatherapp.data.remote.api.WeatherApi
import com.myintag.weatherapp.data.remote.repository.WeatherRepositoryImpl
import com.myintag.weatherapp.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.NONE // Set the desired logging level
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient())
            .build()

    @Provides
    @Singleton
    fun provideGeocodingApi(retrofit: Retrofit): GeocodingApi =
        retrofit.create(GeocodingApi::class.java)

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi =
        retrofit.create(WeatherApi::class.java)

    @Provides
    @Singleton
    fun provideApiKey(): String = BuildConfig.OPENWEATHER_API_KEY

    @Provides
    @Singleton
    fun provideWeatherRepository(
        geocodingApi: GeocodingApi,
        weatherApi: WeatherApi,
        apiKey: String
    ): WeatherRepository = WeatherRepositoryImpl(geocodingApi, weatherApi, apiKey)
} 