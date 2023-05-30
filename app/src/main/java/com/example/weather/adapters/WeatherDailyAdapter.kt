package com.example.weather.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.data.DailyItem
import com.example.weather.databinding.ListItemDailyBinding
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

class WeatherDailyAdapter : ListAdapter<DailyItem, WeatherDailyAdapter.Holder>(Comparator()){

    class Holder(view: View):RecyclerView.ViewHolder(view) {
        private val binding = ListItemDailyBinding.bind(view)
        fun bind(item: DailyItem) = with(binding){
            val stringDate = item.time
            val format1 = SimpleDateFormat("yyyy-MM-dd")
            val dateItem: Date = format1.parse(stringDate)
            val c = Calendar.getInstance()
            c.time = dateItem
            val dayOfWeek = c[Calendar.DAY_OF_WEEK]
            val nowDate = LocalDate.now()
            val localDate: LocalDate = LocalDate.parse(item.time)
            if (nowDate == localDate) {
                day.text = "Сегодня"
            }
            else when (dayOfWeek){
                2 -> day.text = "Пн"
                3 -> day.text = "Вт"
                4 -> day.text = "Ср"
                5 -> day.text = "Чт"
                6 -> day.text = "Пт"
                7 -> day.text = "Сб"
                1 -> day.text = "Вс"
            }
            tempNight.text = item.weather_min.toInt().toString().plus("°")
            tempDay.text = item.weather_max.toInt().toString().plus("°")
            when (item.weathercode){
                0  -> {
                    dayWeatherIcon.setImageResource(R.drawable.sun)
                    nightWeatherIcon.setImageResource(R.drawable.night_moon)
                }
                1 -> {
                    dayWeatherIcon.setImageResource(R.drawable.cloud_sun)
                    nightWeatherIcon.setImageResource(R.drawable.cloud_moon)
                }
                2 -> {
                    dayWeatherIcon.setImageResource(R.drawable.cloud_day)
                    nightWeatherIcon.setImageResource(R.drawable.cloud_day)
                }
                3 -> {
                    dayWeatherIcon.setImageResource(R.drawable.cloudy_day)
                    nightWeatherIcon.setImageResource(R.drawable.cloudy_day)
                }
                48 -> {
                    dayWeatherIcon.setImageResource(R.drawable.fog_sun)
                    nightWeatherIcon.setImageResource(R.drawable.fog_moon)
                }
                45 -> {
                    dayWeatherIcon.setImageResource(R.drawable.fog_day)
                    nightWeatherIcon.setImageResource(R.drawable.fog_day)
                }
                51,53,56-> {
                    dayWeatherIcon.setImageResource(R.drawable.light_rain_sun)
                    nightWeatherIcon.setImageResource(R.drawable.light_rain_moon)
                }
                55,57 -> {
                    dayWeatherIcon.setImageResource(R.drawable.light_rain_night)
                    nightWeatherIcon.setImageResource(R.drawable.light_rain_night)
                }
                61, 63,80,81 -> {
                    dayWeatherIcon.setImageResource(R.drawable.heavy_rain_sun)
                    nightWeatherIcon.setImageResource(R.drawable.heavy_rain_moon)
                }
                65,82 -> {
                    dayWeatherIcon.setImageResource(R.drawable.heavy_rain_day)
                    nightWeatherIcon.setImageResource(R.drawable.heavy_rain_day)
                }
                66 -> {
                    dayWeatherIcon.setImageResource(R.drawable.hail_rain_snow_sun)
                    nightWeatherIcon.setImageResource(R.drawable.hail_rain_snow_moon)
                }
                67 -> {
                    dayWeatherIcon.setImageResource(R.drawable.hail_rain_snow_day)
                    nightWeatherIcon.setImageResource(R.drawable.hail_rain_snow_day)
                }
                71,73,85 -> {
                    dayWeatherIcon.setImageResource(R.drawable.snowflakes_sun)
                    nightWeatherIcon.setImageResource(R.drawable.snowflakes_moon)
                }
                75,86 -> {
                    dayWeatherIcon.setImageResource(R.drawable.snowflake_day)
                    nightWeatherIcon.setImageResource(R.drawable.snowflake_day)
                }
                77 -> {
                    dayWeatherIcon.setImageResource(R.drawable.snow_day)
                    nightWeatherIcon.setImageResource(R.drawable.snow_day)
                }
                95 -> {
                    dayWeatherIcon.setImageResource(R.drawable.thunder_sun)
                    nightWeatherIcon.setImageResource(R.drawable.thunder_moon)
                }
                96, 99 ->{
                    dayWeatherIcon.setImageResource(R.drawable.thunder_rain_sun)
                    nightWeatherIcon.setImageResource(R.drawable.thunder_rain_moon)
                }
            }
        }
    }

    class Comparator : DiffUtil.ItemCallback<DailyItem>(){
        override fun areItemsTheSame(oldItem: DailyItem, newItem: DailyItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DailyItem, newItem: DailyItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_daily, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }
}