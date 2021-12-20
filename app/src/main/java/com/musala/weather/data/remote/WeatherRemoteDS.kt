package com.musala.weather.data.remote

import com.musala.weather.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherRemoteDS {
    @GET("weather")
    suspend fun getWeatherDataByCityName(
        @Query("q") cityName: String,
        @Query("appid") appID: String = Constants.API_KEY
    ): WeatherResponse
}