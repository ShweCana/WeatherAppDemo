package com.myintag.weatherapp.presentation.details

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.myintag.weatherapp.presentation.components.WeatherIcon
import com.myintag.weatherapp.utils.provideAppBackground
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.width
import com.myintag.weatherapp.model.HourlyWeather

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    modifier: Modifier,
    lat: Double,
    lon: Double,
    dayTimestamp: Long,
    viewModel: DetailsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(lat, lon, dayTimestamp) {
        viewModel.loadDetails(lat, lon, dayTimestamp)
    }
    when (uiState) {
        is DetailsUIState.Loading -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is DetailsUIState.Error -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    (uiState as DetailsUIState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is DetailsUIState.Success -> {
            val data = (uiState as DetailsUIState.Success).details
            val hourly = (uiState as DetailsUIState.Success).hourly
            DetailsScreenContent(
                modifier = modifier,
                summary = data.summary ?: "",
                cityName = data.weatherMain ?: "Weather",
                temperature = data.tempDay?.toInt() ?: 0,
                weatherDescription = data.weatherDescription ?: "",
                iconCode = data.icon,
                wind = data.windSpeed?.let { "${it} km/h" } ?: "-",
                humidity = data.humidity?.let { "$it%" } ?: "-",
                uvIndex = data.uvi?.let { if (it >= 7) "High" else if (it >= 3) "Moderate" else "Low" }
                    ?: "-",
                pressure = data.pressure?.let { "$it hPa" } ?: "-",
                date = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(
                    Date(
                        dayTimestamp * 1000
                    )
                ),
                hourly = hourly
            )
        }

        DetailsUIState.Empty -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No details available.")
            }
        }
    }
}

@Composable
fun DetailsScreenContent(
    modifier: Modifier,
    summary: String?,
    cityName: String,
    temperature: Int,
    weatherDescription: String,
    iconCode: String?,
    wind: String,
    humidity: String,
    uvIndex: String,
    pressure: String,
    date: String,
    hourly: List<HourlyWeather>? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(provideAppBackground())
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {

            Text(
                text = cityName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$temperature°",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = weatherDescription,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                WeatherIcon(iconCode, modifier = Modifier.size(196.dp))
            }

            Text(
                text = summary.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoCard(title = "WIND", value = wind, modifier = Modifier.weight(1f))
                    InfoCard(title = "HUMIDITY", value = humidity, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoCard(title = "UV INDEX", value = uvIndex, modifier = Modifier.weight(1f))
                    InfoCard(title = "PRESSURE", value = pressure, modifier = Modifier.weight(1f))
                }
            }

            // Hourly chart
            if (!hourly.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Hourly Temperature",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                HourlyTemperatureChart(hourly)

            }
        }
    }
}

@Composable
fun HourlyTemperatureChart(hourly: List<HourlyWeather>) {
    val temps = hourly.mapNotNull { it.temp }
    if (temps.isEmpty()) return
    val minTemp = temps.minOrNull() ?: 0.0
    val maxTemp = temps.maxOrNull() ?: 1.0
    val tempRange = (maxTemp - minTemp).takeIf { it > 0 } ?: 1.0
    val barWidth = 24.dp
    val chartHeight = 100.dp
    Row(
        modifier = Modifier
            .height(160.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        hourly.forEach { hour ->
            val normalized = ((hour.temp ?: minTemp) - minTemp) / tempRange
            val barHeight = (chartHeight.value * normalized).dp
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .height(barHeight)
                        .width(barWidth)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                )
                Text(
                    text = "${hour.temp?.toInt() ?: "-"}°",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun InfoCard(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}