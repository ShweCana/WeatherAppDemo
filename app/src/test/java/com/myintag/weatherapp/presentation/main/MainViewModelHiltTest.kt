package com.myintag.weatherapp.presentation.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.myintag.weatherapp.repository.WeatherRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class MainViewModelHiltTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Inject
    lateinit var weatherRepository: WeatherRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun searchCity_emitsSuccessWithTorontoData() = runTest(testDispatcher) {
        val viewModel = MainViewModel(weatherRepository)
        viewModel.searchCity("Toronto")
        
        // Wait for the state changes
        val state = viewModel.uiState.first { state ->
            state is WeatherUIState.Success || state is WeatherUIState.Error
        }
        
        if (state is WeatherUIState.Error) {
            throw AssertionError("Expected Success state but got Error: ${state.message}")
        }
        
        if (state !is WeatherUIState.Success) {
            throw AssertionError("Expected Success state but got: $state")
        }

        assertEquals("Toronto", state.location.name)
        assertEquals(43.6535, state.location.lat, 0.0001)
        assertEquals(-79.3839, state.location.lon, 0.0001)
        assertEquals(3, state.forecast.size) // We have 3 days in the embedded JSON

        val firstForecast = state.forecast[0]
        assertEquals(1753117200L, firstForecast.date)
        assertEquals("You can expect clear sky in the morning, with partly cloudy in the afternoon", firstForecast.summary)
        assertEquals(22.49, firstForecast.tempDay!!, 0.01)
        assertEquals("Clear", firstForecast.weatherMain)
        assertEquals("01d", firstForecast.icon)
    }

    @Test
    fun searchCity_emitsErrorWhenCityNotFound() = runTest(testDispatcher) {
        val viewModel = MainViewModel(weatherRepository)
        viewModel.searchCity("NonExistentCity")

        val state = viewModel.uiState.first { state ->
            state is WeatherUIState.Error || state is WeatherUIState.Success
        }

        if (state is WeatherUIState.Success) {
            throw AssertionError("Expected Error state but got Success: ${state.location.name}")
        }

        if (state !is WeatherUIState.Error) {
            throw AssertionError("Expected Error state but got: $state")
        }

        assertEquals("City not found", state.message)
    }

    @Test
    fun loadCurrentLocation_emitsSuccessWithCurrentLocationData() = runTest(testDispatcher) {
        val viewModel = MainViewModel(weatherRepository)
        viewModel.loadCurrentLocation(43.6535, -79.3839) // Toronto coordinates

        // Wait for the state changes
        val state = viewModel.uiState.first { state ->
            state is WeatherUIState.Success || state is WeatherUIState.Error
        }

        if (state !is WeatherUIState.Success) {
            throw AssertionError("Expected Success state but got: $state")
        }

        assertEquals("Toronto", state.location.name)
    }
} 