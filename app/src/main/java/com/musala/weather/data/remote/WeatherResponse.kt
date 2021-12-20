package com.musala.weather.data.remote

data class WeatherResponse(
    val base: String, // stations
    val clouds: Clouds,
    val cod: Int, // 200
    val coord: Coord,
    val dt: Int, // 1639859279
    val id: Int, // 360630
    val main: Main,
    val name: String, // Cairo
    val sys: Sys,
    val timezone: Int, // 7200
    val visibility: Int, // 10000
    val weather: List<Weather>,
    val wind: Wind
) {
    data class Clouds(
        val all: Int // 40
    )

    data class Coord(
        val lat: Double, // 30.0626
        val lon: Double // 31.2497
    )

    data class Main(
        val feels_like: Double, // 288.42
        val humidity: Int, // 44
        val pressure: Int, // 1016
        val temp: Double, // 289.57
        val temp_max: Double, // 289.57
        val temp_min: Double // 288.38
    )

    data class Sys(
        val country: String, // EG
        val id: Int, // 2514
        val sunrise: Int, // 1639802719
        val sunset: Int, // 1639839477
        val type: Int // 1
    )

    data class Weather(
        val description: String, // scattered clouds
        val icon: String, // 03n
        val id: Int, // 802
        val main: String // Clouds
    )

    data class Wind(
        val deg: Int, // 220
        val speed: Double // 4.12
    )
}