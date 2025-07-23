package com.myintag.weatherapp.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myintag.weatherapp.model.WeatherDetails
import com.myintag.weatherapp.model.HourlyWeather
import com.myintag.weatherapp.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetailsUIState {
    object Loading : DetailsUIState()
    data class Success(val details: WeatherDetails, val hourly: List<HourlyWeather>?) : DetailsUIState()
    data class Error(val message: String) : DetailsUIState()
    object Empty : DetailsUIState()
}

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailsUIState>(DetailsUIState.Empty)
    val uiState: StateFlow<DetailsUIState> = _uiState

    fun loadDetails(lat: Double, lon: Double, dayTimestamp: Long) {
        viewModelScope.launch {
            _uiState.value = DetailsUIState.Loading
            try {
                val details = weatherRepository.getWeatherDetails(lat, lon, dayTimestamp)
                if (details != null) {
                    _uiState.value = DetailsUIState.Success(details, details.hourly)
                } else {
                    _uiState.value = DetailsUIState.Error("No details available.")
                }
            } catch (e: Exception) {
                _uiState.value = DetailsUIState.Error(e.message ?: "Unknown error")
            }
        }
    }
} 