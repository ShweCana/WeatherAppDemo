package com.myintag.weatherapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

@Composable
fun WeatherIcon(iconCode: String?, modifier: Modifier = Modifier) {
    val url = iconCode?.let { "https://openweathermap.org/img/wn/${it}@4x.png" }
    if (url != null) {
        Image(
            painter = rememberAsyncImagePainter(url),
            contentDescription = "Weather icon",
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    }
} 