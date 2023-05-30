package com.example.weather.data

data class WeatherModel(
    val city: String,
    val weather: String,
    val weatherMain: String,
    val temp: Double,
    val feels_like: Double,
    val grnd_level: Int,
    val humidity: Int,
    val visibility: Int,
    val cloudiness: Int,
    val pressure: Int,
    val wind_speed: Double,
    val wind_direction: Int,
    val sea_level: Int,
    val temp_max: Double,
    val temp_min: Double,
    val timezone: Int,
    val sunrise: Int,
    val sunset: Int
)