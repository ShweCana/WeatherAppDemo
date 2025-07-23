package com.myintag.weatherapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.myintag.weatherapp.model.DailyForecast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ForecastList(
    forecast: List<DailyForecast>,
    onDaySelected: (Long) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        forecast.forEach { day ->
            val shape = RoundedCornerShape(12.dp)
            Surface (
                modifier = Modifier
                    .padding(8.dp)
                    .width(110.dp)
                    .clip(shape)
                    .clickable { onDaySelected(day.date) },
                shape = shape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val date = remember(day.date) {
                        SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(day.date * 1000))
                    }
                    Text(date, color = MaterialTheme.colorScheme.onSurface)
                    WeatherIcon(day.icon, modifier = Modifier.size(48.dp))
                    Text("${day.tempMin?.toInt() ?: "-"}° / ${day.tempMax?.toInt() ?: "-"}°", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
} 