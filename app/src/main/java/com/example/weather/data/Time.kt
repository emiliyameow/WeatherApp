package com.example.weather.data

import com.example.weather.R

enum class Time {
    MORNING,
    DAY,
    EVENING,
    NIGHT;
}

public fun getTime(hour: Int): Time {
    return when {
        hour in 22 .. 24  || hour in 0..6 -> Time.NIGHT
        hour in 18..21 ->  Time.EVENING
        hour in 12..15 ->  Time.DAY
        hour in 7..11 ->  Time.MORNING
        else -> Time.DAY
    }
}
public fun getImageForRain(time: Time): Int {
    return getImage(
        time,
        drawableMorning = R.drawable.back_rain_sunrise,
        drawableDay = R.drawable.back_rain_day_,
        drawableEvening = R.drawable.back_rain_evening_,
        drawableNight = R.drawable.back_rain_night
    )
}

public fun getImageForCloudy(time: Time): Int {
    return getImage(
        time,
        drawableMorning = R.drawable.back_cloudy_morning_,
        drawableDay = R.drawable.back_cloudy_day7,
        drawableEvening = R.drawable.back_cloudy_evening_,
        drawableNight = R.drawable.back_cloudy_night_
    )
}

public fun getImageForDrizzle(time: Time): Int {
    return getImage(
        time,
        drawableMorning = R.drawable.back_dizzy_sunrise,
        drawableDay = R.drawable.back_dizzy_day4,
        drawableEvening = R.drawable.back_dizzy_sunset,
        drawableNight = R.drawable.back_dizzy_day3
    )
}

public fun getImageForFog(time: Time): Int {
    return getImage(
        time,
        drawableMorning = R.drawable.back_fog,
        drawableDay = R.drawable.back_fog,
        drawableEvening = R.drawable.back_strong_fog,
        drawableNight = R.drawable.back_strong_fog
    )
}

public fun getImageForClear(time: Time): Int {
    return getImage(
        time,
        drawableMorning = R.drawable.back_almost_clean_day2,
        drawableDay = R.drawable.back_sunset_cloudy_day2,
        drawableEvening = R.drawable.back_sunset_clean,
        drawableNight = R.drawable.back_night_stars
    )
}

public fun getImageForSnow(time: Time): Int {
    return getImage(
        time,
        drawableMorning = R.drawable.back_snow_day,
        drawableDay = R.drawable.back_snow_day,
        drawableEvening = R.drawable.back_snow_day2,
        drawableNight = R.drawable.back_snow_night
    )
}
public fun getImageForThunder(time: Time): Int {
    return getImage(
        time,
        drawableMorning = R.drawable.back_thunder_morning,
        drawableDay = R.drawable.back_thunder_day,
        drawableEvening = R.drawable.back_thunder_evening,
        drawableNight = R.drawable.back_thunder_night
    )
}
private fun getImage(
    time: Time,
    drawableMorning: Int,
    drawableDay: Int,
    drawableEvening: Int,
    drawableNight: Int,
): Int {
    return when (time) {
        Time.MORNING -> drawableMorning
        Time.DAY -> drawableDay
        Time.EVENING -> drawableEvening
        Time.NIGHT -> drawableNight
    }
}