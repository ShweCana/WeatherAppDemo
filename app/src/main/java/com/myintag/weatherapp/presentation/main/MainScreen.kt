package com.myintag.weatherapp.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.myintag.weatherapp.checkIfLocationPermitted
import com.myintag.weatherapp.presentation.components.ForecastList
import com.myintag.weatherapp.presentation.components.WeatherIcon
import com.myintag.weatherapp.utils.provideAppBackground

@Composable
fun MainScreen(
    onDaySelected: (Double, Double, Long) -> Unit,
    onDataLoaded: (Double, Double, Long) -> Unit,
    viewModel: MainViewModel,
    onGrantPermissionClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchText = remember { androidx.compose.runtime.mutableStateOf("") }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                provideAppBackground()
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = searchText.value,
                onValueChange = { searchText.value = it },
                label = { Text("Search city", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.searchCity(searchText.value)
                        keyboardController?.hide()
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = { 
                        viewModel.searchCity(searchText.value)
                        keyboardController?.hide()
                    }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground)
            )
            Spacer(modifier = Modifier.height(16.dp))

            when (uiState) {
                is WeatherUIState.Loading -> {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is WeatherUIState.NoInternet -> {
                    Text("No internet connection", color = MaterialTheme.colorScheme.error)
                }

                is WeatherUIState.Error -> {
                    Text(
                        (uiState as WeatherUIState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is WeatherUIState.Success -> {
                    val state = uiState as WeatherUIState.Success
                    Text(
                        state.location.name,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (state.forecast.isNotEmpty()) {
                        val today = state.forecast.first()
                        onDataLoaded(state.location.lat, state.location.lon, today.date)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            WeatherIcon(today.icon, modifier = Modifier.size(196.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "${today.tempDay?.toInt() ?: "-"}°",
                                    style = MaterialTheme.typography.displayLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    today.weatherMain ?: "",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = today.summary.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            "${state.forecast.size}-day Forecast",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ForecastList(forecast = state.forecast, onDaySelected = { dayTimestamp ->
                            onDaySelected(state.location.lat, state.location.lon, dayTimestamp)
                        })
                    }
                }

                WeatherUIState.Empty -> {
                    Text(
                        "Search for a city to see the weather.",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            if (!checkIfLocationPermitted(context)) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        onGrantPermissionClicked()
                    }) {
                        Text("Grant location access")
                    }
                }
            }
        }
    }
} 