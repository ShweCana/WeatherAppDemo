package com.myintag.weatherapp.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import com.myintag.weatherapp.ui.theme.Blueish
import com.myintag.weatherapp.ui.theme.DarkBlueGray
import com.myintag.weatherapp.ui.theme.DarkerGray
import com.myintag.weatherapp.ui.theme.LightBlueGray

@Composable
fun provideAppBackground(): Brush {
    return if (isSystemInDarkTheme()) {
        // Dark mode gradient
        Brush.verticalGradient(
            colors = listOf(
                DarkBlueGray,
                DarkerGray  // darker gray
            )
        )
    } else {
        // Light mode gradient
        Brush.verticalGradient(
            colors = listOf(
                Blueish,
                LightBlueGray
            )
        )
    }
} 