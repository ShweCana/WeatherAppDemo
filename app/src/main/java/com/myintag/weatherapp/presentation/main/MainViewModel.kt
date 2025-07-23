package com.myintag.weatherapp.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myintag.weatherapp.model.DailyForecast
import com.myintag.weatherapp.model.Location
import com.myintag.weatherapp.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

sealed class WeatherUIState {
    object Loading : WeatherUIState()
    data class Success(val location: Location, val forecast: List<DailyForecast>) : WeatherUIState()
    data class Error(val message: String) : WeatherUIState()
    object Empty : WeatherUIState()
    object NoInternet : WeatherUIState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<WeatherUIState>(WeatherUIState.Empty)
    val uiState: StateFlow<WeatherUIState> = _uiState

    fun searchCity(city: String) {
        viewModelScope.launch {
            _uiState.value = WeatherUIState.Loading
            try {
                val loc = weatherRepository.getLocationByCity(city)
                if (loc != null) {
                    val forecast = weatherRepository.getWeekForecast(loc.lat, loc.lon)
                    _uiState.value = WeatherUIState.Success(loc, forecast)
                } else {
                    _uiState.value = WeatherUIState.Error("City not found")
                }
            } catch (ex: UnknownHostException) {
                _uiState.value = WeatherUIState.NoInternet
            } catch (e: Exception) {
                _uiState.value = WeatherUIState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadCurrentLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = WeatherUIState.Loading
            try {
                val forecast = weatherRepository.getWeekForecast(lat, lon)
                val city = weatherRepository.getCityByCoordinates(lat, lon)
                _uiState.value = WeatherUIState.Success(
                    Location(city?.name ?: "Current Location", lat, lon, null, null),
                    forecast
                )
            } catch (ex: UnknownHostException) {
                _uiState.value = WeatherUIState.NoInternet
            } catch (e: Exception) {
                _uiState.value = WeatherUIState.Error(e.message ?: "Unknown error")
            }
        }
    }
} 