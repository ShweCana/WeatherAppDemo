# WeatherApp

Weather app built with Jetpack Compose, MVVM, Hilt, and Retrofit, using the OpenWeatherMap API.

## Features

- Search weather by city name
- Shows current location weather by default (location permission required)
- Main screen: City name, today's weather, and future forecast
- Details screen: Tap a day for wind, humidity, pressure, visibility, and more
- Two-pane layout: On tablets/landscape, see both main and details side by side
- Light and dark mode support
- Clean architecture: MVVM, repository pattern, DI with Hilt

## Getting Started

### Prerequisites
- Android Studio (Narwhal or newer recommended)
- OpenWeatherMap API key ([sign up here](https://home.openweathermap.org/users/sign_up))
- Uses [OpenWeatherMap One Call API 3.0](https://openweathermap.org/api/one-call-3) for weather data
- Uses [OpenWeatherMap Geocoding API](https://openweathermap.org/api/geocoding-api) for city-to-coordinates

### Setup
1. **Add your API key**
   - Open `local.properties`
   - Add:
     ```
     OPENWEATHER_API_KEY=your_actual_api_key_here
     ```
2. **Sync Gradle**
3. **Build and run the app**

