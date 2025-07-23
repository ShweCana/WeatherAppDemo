@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.myintag.weatherapp.presentation.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.myintag.weatherapp.presentation.details.DetailsScreen
import com.myintag.weatherapp.presentation.main.MainScreen
import com.myintag.weatherapp.presentation.main.MainViewModel

object Routes {
    const val MAIN = "main"
    const val DETAILS = "details/{lat}/{lon}/{dayTimestamp}"
    fun details(lat: Double, lon: Double, dayTimestamp: Long) = "details/$lat/$lon/$dayTimestamp"
}

@Composable
fun WeatherNavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel,
    onGrantPermissionClicked: () -> Unit,
    onDataLoaded: (Double, Double, Long) -> Unit,
) {
    NavHost(navController = navController, startDestination = Routes.MAIN) {
        composable(Routes.MAIN) {
            MainScreen(
                onDaySelected = { lat, lon, dayTimestamp ->
                    navController.navigate(Routes.details(lat, lon, dayTimestamp))
                },
                onDataLoaded = { lat, lon, dayTimestamp ->
                    onDataLoaded(lat, lon, dayTimestamp)
                },
                viewModel = viewModel,
                onGrantPermissionClicked = {
                    onGrantPermissionClicked()
                }
            )
        }
        composable(
            route = Routes.DETAILS,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lon") { type = NavType.StringType },
                navArgument("dayTimestamp") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
            val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull() ?: 0.0
            val dayTimestamp = backStackEntry.arguments?.getLong("dayTimestamp") ?: 0L

            Scaffold(
                modifier = Modifier
                    .fillMaxWidth(),
                topBar = {
                    TopAppBar(
                        title = { Text("Weather Details", style = MaterialTheme.typography.titleLarge) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
            ) { innerPadding ->
                DetailsScreen(
                    modifier = Modifier.padding(innerPadding),
                    lat = lat,
                    lon = lon,
                    dayTimestamp = dayTimestamp
                )
            }
        }
    }
} 