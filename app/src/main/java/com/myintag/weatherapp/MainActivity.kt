package com.myintag.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import com.myintag.weatherapp.presentation.components.TwoPaneLayout
import com.myintag.weatherapp.presentation.details.DetailsScreen
import com.myintag.weatherapp.presentation.main.MainScreen
import com.myintag.weatherapp.presentation.main.MainViewModel
import com.myintag.weatherapp.presentation.navigation.WeatherNavGraph
import com.myintag.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint

private const val INITIAL_LOCATION = "initialLocation"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var initialLocation: Pair<Double, Double>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge with transparent navigation bar
        setupTransparentSystemBars()

        if (checkIfLocationPermitted(this)) {
            requestLocationAndStart()
        } else {
            setContentWithLocation(null)
        }

        // Register before setContent!
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                requestLocationAndStart()
            } else {
                setContentWithLocation(null)
            }
        }
    }

    // To survive orientation changes, save the previous location
    // before activity gets recreated
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDoubleArray(INITIAL_LOCATION, initialLocation?.let { doubleArrayOf(it.first, it.second) })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getDoubleArray(INITIAL_LOCATION)?.let {
            initialLocation = Pair(it[0], it[1])
        }
    }

    @SuppressLint("MissingPermission") // safe to suppress as we've already done the permission check
    private fun requestLocationAndStart() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (initialLocation != null) {
                setContentWithLocation(initialLocation)
            } else if (location != null) {
                initialLocation = Pair(location.latitude, location.longitude)
                setContentWithLocation(initialLocation)
            } else {
                setContentWithLocation(null)
            }
        }
    }

    private fun setContentWithLocation(location: Pair<Double, Double>?) {
        setContent {
            val configuration = LocalConfiguration.current
            val screenWidthDp = configuration.screenWidthDp
            val navController = rememberNavController()
            var selectedDay by remember { mutableStateOf<Triple<Double, Double, Long>?>(null) }
            val viewModel = hiltViewModel<MainViewModel>()

            // On first composition, if location is available, load weather
            LaunchedEffect(location) {
                location?.let { (lat, lon) ->
                    viewModel.loadCurrentLocation(lat, lon)
                }
            }

            WeatherAppTheme {
                if (screenWidthDp >= 600) {
                    TwoPaneLayout(
                        mainContent = {
                            MainScreen(
                                onDaySelected = { lat, lon, dayTimestamp ->
                                    selectedDay = Triple(lat, lon, dayTimestamp)
                                },
                                onDataLoaded = { lat, lon, dayTimestamp ->
                                    initialLocation = Pair(lat, lon)
                                    selectedDay = Triple(lat, lon, dayTimestamp)
                                },
                                viewModel = viewModel,
                                onGrantPermissionClicked = {
                                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            )
                        },
                        detailContent = {
                            val day = selectedDay
                            if (day != null) {
                                DetailsScreen(
                                    modifier = Modifier,
                                    lat = day.first,
                                    lon = day.second,
                                    dayTimestamp = day.third
                                )
                            }
                        }
                    )
                } else {
                    WeatherNavGraph(navController, viewModel,
                        onGrantPermissionClicked = {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        },
                        onDataLoaded = { lat, lon, dayTimestamp ->
                            initialLocation = Pair(lat, lon)
                        })
                }
            }
        }
    }

    private fun setupTransparentSystemBars() {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            )
        )

        // In Samsung devices, navigation bar is overlapping UI in landscape mode
        // make the bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }
    }
}

fun checkIfLocationPermitted(context: Context): Boolean {
    return (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    )
}