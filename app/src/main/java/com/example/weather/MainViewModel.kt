package com.example.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.data.CityItem
import com.example.weather.data.DataCity
import com.example.weather.data.WeatherModel

class MainViewModel :ViewModel() {
    val liveDataCurrent = MutableLiveData<WeatherModel>()
    val cityData = MutableLiveData<CityItem>()
}