package com.example.weather

data class WeatherModel(
    val temp: Double,
    val feels_like: Double,
    val grnd_level: Int,
    val humidity: Int,
    val pressure: Int,
    val sea_level: Int,
    val temp_max: Double,
    val temp_min: Double
)