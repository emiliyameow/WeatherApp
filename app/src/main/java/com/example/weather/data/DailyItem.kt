package com.example.weather.data
/*
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
    */

 data class DailyItem (
    val weather_min : Double,
    val weather_max : Double,
    val weathercode : Int,
    val time : String
)