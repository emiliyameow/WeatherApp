package com.example.weather

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather.adapters.DialogManager
import com.example.weather.adapters.WeatherDailyAdapter
import com.example.weather.data.CityItem
import com.example.weather.data.DailyItem
import com.example.weather.data.WeatherModel
import com.example.weather.databinding.FragmentMainBinding
import com.example.weather.retrofit.MeteoApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalTime
import kotlin.math.roundToInt


class MainFragment : Fragment() {
    private lateinit var fLocationClient: FusedLocationProviderClient
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: WeatherDailyAdapter
    private val model : MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        init()
        getLocation()
        updateCurrentWeather()
    }

    private fun permissionListener(){
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        }
    }

    private fun init() = with(binding){
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        searchButton.setOnClickListener {
            DialogManager.searchByNameDialog(requireContext(), object :
            DialogManager.Listener{
                override fun onClick(name: String?){
                    name?.let{it1 -> getWeatherByCity(name)}
                }
            })
        }
    }

    private fun initRecyclerView(items : List<DailyItem>) = with(binding){
        dailyForecast.layoutManager = LinearLayoutManager(activity)
        adapter = WeatherDailyAdapter()
        dailyForecast.adapter = adapter
        adapter.submitList(items)
        weatherDaily.visibility = View.VISIBLE
    }

    private fun isLocationEnabled(): Boolean{
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation(){
        val ct = CancellationTokenSource()
        if (!isLocationEnabled()){
            Toast.makeText(requireContext(), getString(R.string.no_acess), Toast.LENGTH_SHORT).show()
            return
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener{
                getCurrentWeather(it.result.latitude,it.result.longitude)
            }
    }

    private fun checkPermission (){
        if (!isPermissionGranted(android.Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListener()
            pLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }
    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()
    }

    private fun getWeatherByCity(result: String){
        val url = "https://api.openweathermap.org/geo/1.0/direct?" +
                "q="+result+"&limit=5&appid=$API_KEY"

        Log.d("MyLog","Url: $url")

        val queue = Volley.newRequestQueue(requireContext())
        val stringRequest = StringRequest(Request.Method.GET,
            url,
            {
                    response-> parseCityData(response)
            }, {
                Log.d("MyLog","Volley error: $it")
            }
        )
        queue.add(stringRequest)
        model.cityData.observe(viewLifecycleOwner){
            getDailyWeather(model.cityData.value!!.lat, model.cityData.value!!.long)
            getCurrentWeather(model.cityData.value!!.lat, model.cityData.value!!.long)
        }

    }

    private fun parseCityData(result: String){
        val mainObject = JSONArray(result)
        val weatherFirst = mainObject[0] as JSONObject
        val item = CityItem(
            weatherFirst.getString("name"),
            weatherFirst.getDouble("lat"),
            weatherFirst.getDouble("lon")
        )
        model.cityData.value = item

        Log.d("MyLog","Temp: $item")
    }

    private fun getCurrentWeather(latitude: Double, longitude: Double) {

        val url = "https://api.openweathermap.org/data/2.5/weather?" +
                "lat="+latitude.toString()+"&lon="+longitude.toString()+"&units=metric&appid=$API_KEY&lang=ru"
        Log.d("MyLog", "longitude:$longitude");
        Log.d("MyLog", "latitude:$latitude");
        Log.d("MyLog","Url: $url")

        val queue = Volley.newRequestQueue(requireContext())
        val stringRequest = StringRequest(Request.Method.GET,
            url,
            {
                    response-> parseWeatherData(response)
                Log.d("MyLog","Everything is right!!: $response")
            }, {
                Log.d("MyLog","Volley error: $it")
            }
        )
        queue.add(stringRequest)

        getDailyWeather(latitude, longitude)
    }


    private fun getDailyWeather(latitude: Double, longitude: Double) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val items = mutableListOf<DailyItem>()
        CoroutineScope(Dispatchers.Main).launch {
            val api = retrofit.create(MeteoApi::class.java)
            val dailyModell = api.getDailyWeatherByCoordinates(
                latitude,
                longitude,
                "temperature_2m_max,temperature_2m_min,weathercode",
                "auto")

            for (i in 0..6){
                items.add(i, DailyItem(
                    dailyModell.daily.temperature_2m_min[i],
                    dailyModell.daily.temperature_2m_max[i],
                    dailyModell.daily.weathercode[i],
                    dailyModell.daily.time[i]))
            }

            Log.d("MyLog","Url: $dailyModell")
            initRecyclerView(items)
        }
    }

    private fun updateCurrentWeather() = with (binding){
        model.liveDataCurrent.observe(viewLifecycleOwner){
            city.text = it.city
            weather.text=it.weather
            weather.visibility= View.VISIBLE
            currentTemp.text = (it.temp.roundToInt()).toString()+getString(R.string.Degree)
            maxAndMinTemp.text=getString(R.string.max_temp)+it.temp_max.roundToInt().toString()+getString(
                            R.string.min_temp)+ it.temp_min.roundToInt().toString()+getString(R.string.Degree)
            maxAndMinTemp.visibility = View.VISIBLE

            val unixSunrise = it.sunrise+it.timezone
            val unixSunset = it.sunset+it.timezone
            sunrise.text= getString(R.string.sunrise)+java.time.format
                .DateTimeFormatter.ISO_INSTANT
                .format(java.time.Instant.ofEpochSecond(unixSunrise.toLong()))
                .substringAfter('T').substringBeforeLast(':')
            sunset.text=java.time.format.DateTimeFormatter.ISO_INSTANT
                .format(java.time.Instant.ofEpochSecond(unixSunset.toLong())).
                substringAfter('T').substringBeforeLast(':')
            scrollViewBackground.setBackgroundResource(R.drawable.cloudy_background)

            val time = LocalTime.now()
            when (it.weatherMain){
                "Thunderstorm" -> {
                    scrollViewBackground.setBackgroundResource(R.drawable.thunder_sun)
                }
                "Drizzle" -> {
                    scrollViewBackground.setBackgroundResource(R.drawable.light_rain_sun)
                }
                "Rain" -> {
                    scrollViewBackground.setBackgroundResource(R.drawable.back_rain_day)
                }
                "Snow" -> {
                    scrollViewBackground.setBackgroundResource(R.drawable.snow_day)
                }
                "Clear" -> {
                    scrollViewBackground.setBackgroundResource(R.drawable.back_clean_day)
                }
                "Clouds" -> {
                    scrollViewBackground.setBackgroundResource(R.drawable.back_cloud_day)
                }
                else -> {
                    scrollViewBackground.setBackgroundResource(R.drawable.fog_day)
                }

            }


            visibility.text=(it.visibility/1000).toString()+" км"
            if (it.visibility/1000>=10){
                visibilityDescription.text=getString(R.string.clear_sky)
            }
            else {
                if (it.visibility/1000<3)
                    visibilityDescription.text=getString(R.string.not_clear_sky)
                else
                    visibilityDescription.text=getString(R.string.not_not_clear_sky)
            }

            wind.text=getString(R.string.speed)+it.wind_speed.roundToInt().toString()+getString(R.string.ms)
            val windDirectionInt = it.wind_direction
            when (windDirectionInt){
                90 -> windDirection.text=getString(R.string.east)
                180 -> windDirection.text=getString(R.string.south)
                240 -> windDirection.text=getString(R.string.west)
                360 -> windDirection.text=getString(R.string.north)
                else ->{
                    if (windDirectionInt<90){
                        windDirection.text=getString(R.string.ne)
                    }
                    else if (windDirectionInt in 91..179){
                        windDirection.text=getString(R.string.se)
                    }
                    else if (windDirectionInt in 181..239){
                        windDirection.text=getString(R.string.sw)
                    }
                    else {
                        windDirection.text=getString(R.string.nw)
                    }
                }
            }
            cloudiness.text=it.cloudiness.toString()+getString(R.string.percent)
            if (it.cloudiness<15)
                cloudinessDescription.text=getString(R.string.clean_sky)
            else
                cloudinessDescription.text=getString(R.string.nonclear_sky)

            humidity.text=it.humidity.toString()+getString(R.string.percent)

            if (it.humidity>80)
                humidityDescription.text=getString(R.string.high_humidity)
            if (it.humidity in 36..79)
                humidityDescription.text=getString(R.string.middle_humidity)
            if (it.humidity<=35)
                humidityDescription.text=getString(R.string.low_humidity)

            feelsLike.text=it.feels_like.roundToInt().toString()+"°"
            if (it.feels_like.roundToInt()==it.temp.roundToInt()){
                feelsLikeDescription.text=getString(R.string.fact_temp)
            }
            else if (it.feels_like.roundToInt()<it.temp.roundToInt()){
                feelsLikeDescription.text=getString(R.string.colder_temp)
            }
            else {
                feelsLikeDescription.text=getString(R.string.warmer_temp)
            }
            textBlocked.visibility = View.INVISIBLE
            sunsetAndVisibility.visibility = View.VISIBLE
            windAndCloudiness.visibility = View.VISIBLE
            feelsLikeAndHumidity.visibility = View.VISIBLE
           }
    }

    private fun parseWeatherData(result: String){
        val mainObject = JSONObject(result)
        val weatherArray = mainObject.getJSONArray("weather")
        val weatherFirst = weatherArray[0] as JSONObject
        val item = WeatherModel(
            mainObject.getString("name"),
            weatherFirst.getString("description"),
            weatherFirst.getString("main"),
            mainObject.getJSONObject("main").getDouble("temp"),
            mainObject.getJSONObject("main").getDouble("feels_like"),
            0,
            mainObject.getJSONObject("main").getInt("humidity"),
            mainObject.getInt("visibility"),
            mainObject.getJSONObject("clouds").getInt("all"),
            mainObject.getJSONObject("main").getInt("pressure"),
            mainObject.getJSONObject("wind").getDouble("speed"),
            mainObject.getJSONObject("wind").getInt("deg"),
            0,
            mainObject.getJSONObject("main").getDouble("temp_max"),
            mainObject.getJSONObject("main").getDouble("temp_min"),
            mainObject.getInt("timezone"),
            mainObject.getJSONObject("sys").getInt("sunrise"),
            mainObject.getJSONObject("sys").getInt("sunset")
            )
        model.liveDataCurrent.value = item
    }
}

const val API_KEY = ""