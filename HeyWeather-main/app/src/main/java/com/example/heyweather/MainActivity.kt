package com.example.heyweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import com.example.heyweather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// fb288a5da26949aa1ab771d105943d18
class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherData("new york")
        SearchCity()
    }

   private fun SearchCity() {
        val searchView = binding.searchView
       searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
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
            .build().create(APIInterface::class.java)

        val response =
            retrofit.getWeatherData(cityName, "fb288a5da26949aa1ab771d105943d18", "matric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toInt()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max.toInt()
                    val minTemp = responseBody.main.temp_min.toInt()

                    val temperature2 = temperature-272
                    val maxTemp2 = maxTemp- 272
                    val minTemp2 = minTemp- 272

                    binding.temp.text = "$temperature2 °C"
                    binding.weather.text = condition
                    binding.maxtemp.text ="Max Temp : $maxTemp2 °C"
                    binding.mintemp.text = "Min Temp : $minTemp2 °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed M/S"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sealevel.text = "$seaLevel hPa"
                    binding.condition.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityname.text = "$cityName"


                   // Log.d("TAG", "onResponse: $temperature")

                    changImagesAccordingToWeather(condition)
                }
            }
            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun changImagesAccordingToWeather(conditions: String) {
        when(conditions){

            "Clear Sky" , "Sunny" , "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunnybg)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Partly Clouds","Clouds","Overcast","Mist","Foggy"  -> {
                binding.root.setBackgroundResource(R.drawable.cloudyweather)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Rain" ,"Light Rain" ,"Drizzle" , "Moderate Rain" , "Showers" , "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Snow", "Light Snow","Moderate Snow" , "Heavy Snow" , "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snowbg3)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunnybg)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }

        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMM YYYY", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}


