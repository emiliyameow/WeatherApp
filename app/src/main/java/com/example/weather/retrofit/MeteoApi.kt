package com.example.weather.retrofit

import com.example.weather.data.DailyModel
import retrofit2.http.GET
import retrofit2.http.Query

interface MeteoApi{
    @GET("forecast?daily=temperature_2m_max,temperature_2m_min&timezone=auto")
    suspend fun getDailyWeatherByCoordinates(
        @Query("latitude") latitude : Double,
        @Query("longitude") longitude : Double,
        @Query("daily") daily : String,
        @Query("timezone") timezone : String
    ): DailyModel
}

/*
https://api.open-meteo.com/v1/forecast?latitude=54.74&longitude=55.97&daily=temperature_2m_max,temperature_2m_min,weathercode&timezone=auto

 {"latitude":54.75,"longitude":56.0,
 "generationtime_ms":1.6380548477172852,
 "utc_offset_seconds":18000,
 "timezone":"Asia/Yekaterinburg",
 "timezone_abbreviation":"+05",
 "elevation":171.0,
 "daily_units":{
 "time":"iso8601",
 "temperature_2m_max":"°C",
 "temperature_2m_min":"°C",
 "weathercode":"wmo code"},
 "daily":{"
 time":["2023-05-26",
 "2023-05-27",
 "2023-05-28",
 "2023-05-29",
 "2023-05-30",
 "2023-05-31",
 "2023-06-01"],
 "temperature_2m_max":[30.2,26.2,32.0,29.8,29.5,30.9,30.6],
 "temperature_2m_min":[11.9,15.0,16.0,18.5,17.2,17.4,18.5],
 "weathercode":[3,61,3,3,2,3,61]
 }
 }*/