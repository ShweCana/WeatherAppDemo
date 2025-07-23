package com.myintag.weatherapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun TwoPaneLayout(
    mainContent: @Composable () -> Unit,
    detailContent: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    if (screenWidthDp >= 600) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                mainContent()
            }
            Spacer(modifier = Modifier.padding(1.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                detailContent()
            }
        }
    } else {
        mainContent()
    }
} 