package com.example.weather

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class MainActivity : AppCompatActivity(), LocationListener {
    var txtLng: TextView? = null
    var txtLat: TextView? = null
    private var latitude = 0.0
    private var longitude = 0.0
    var locationManager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtLng = findViewById<View>(R.id.city) as TextView
        //txtLat = findViewById<View>(R.id.lata) as TextView
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }

        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        //getCurrentWeather(longitude,latitude);
    }

    private fun getCurrentWeather(longitude: Double, latitude: Double) {

        val url = "https://api.openweathermap.org/data/2.5/weather?" +
                "lat="+latitude.toString()+"&lon="+longitude.toString()+"&units=metric&appid=$API_KEY&lang=ru"
        Log.d("MyLog","longitude:"+longitude.toString());
        Log.d("MyLog","Url: $url")

        val queue = Volley.newRequestQueue(this)
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
    }

    override fun onLocationChanged(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
        //txtLat!!.text = latitude.toString()
        txtLng!!.text = longitude.toString()
        locationManager!!.removeUpdates(this)
        getCurrentWeather(longitude, latitude)
    }
    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        //LocationListener.super.onStatusChanged(provider, status, extras);
    }

    override fun onProviderEnabled(provider: String) {
        //LocationListener.super.onProviderEnabled(provider);
    }

    override fun onProviderDisabled(provider: String) {
        //LocationListener.super.onProviderDisabled(provider);
    }
    private fun parseWeatherData(result: String){
        val mainObject = JSONObject(result)
        val item = WeatherModel(
            mainObject.getJSONObject("main").getDouble("temp"),
            mainObject.getJSONObject("main").getDouble("feels_like"),
            //mainObject.getJSONObject("main").getInt("grnd_level"),
            0,
            mainObject.getJSONObject("main").getInt("humidity"),
            mainObject.getJSONObject("main").getInt("pressure"),
            //mainObject.getJSONObject("main").getInt("sea_level"),
            0,
            mainObject.getJSONObject("main").getDouble("temp_max"),
            mainObject.getJSONObject("main").getDouble("temp_min")
        )
        Log.d("MyLog","Temp: ${item.temp}")
    }
}