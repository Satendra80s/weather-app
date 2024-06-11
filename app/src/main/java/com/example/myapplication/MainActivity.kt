package com.example.myapplication

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import retrofit2.Callback
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

// 19ca18ba99652bf42422dc9fb5cfa91e
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
   // var bnView: BottomNavigationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fetchWeatherData("jaipur")
       searchCity()
    }

    private fun searchCity() {
       val searchView=binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName,"19ca18ba99652bf42422dc9fb5cfa91e","metric" )
        response.enqueue(object : Callback<Weatherapp1>{
            override fun onResponse(call: Call<Weatherapp1>, response: Response<Weatherapp1>) {
               val responseBody = response.body()
                if (response.isSuccessful && responseBody != null)
                {
                   val temperature = responseBody.main.temp.toString()

                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val  sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val sealevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknow"
                    val maxtemp = responseBody.main.temp_max
                    val mintemp = responseBody.main.temp_min
                    binding.temp.text="$temperature °C "
                    binding.weather.text  =condition
                    binding.maxTemp.text= "Max Temp: $maxtemp °C"
                    binding.minTemp.text= "Min Temp: $mintemp °C"
                    binding.humidiy.text="$humidity %"
                    binding.windspeed.text="$windSpeed m/s"
                    binding.sunrise.text="${time(sunRise)}"
                    binding.sunset.text="${time(sunSet)}"
                    binding.sea.text="$sealevel"
                    binding.condition.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                        binding.date.text= date()
                        binding.cityname.text="$cityName"



                   // Log.d("TAG", "onResponse: $temperature")


                    changeImageAccordingWeather(condition)
                }
            }

            override fun onFailure(call: Call<Weatherapp1>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }
    fun changeImageAccordingWeather(conditions: String){
        when(conditions){
           "Haze" ->{
               binding.root.setBackgroundResource(R.drawable.colud_background)
               binding.lottieAnimationView.setAnimation(R.raw.cloud)

           }
            "Clear Sky","Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Snow", "Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)

            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }
    fun date():String{
        val sdf =SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun time(timestamp: Long):String{
        val sdf =SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
    fun dayName(timestamp: Long): String{
        val sdf =SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}


