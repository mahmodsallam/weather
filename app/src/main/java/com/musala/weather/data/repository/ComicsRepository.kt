package com.musala.weather.data.repository

import com.musala.weather.data.remote.WeatherRemoteDS
import com.musala.weather.data.remote.WeatherResponse
import javax.inject.Inject

class ComicsRepository @Inject constructor(private val weatherRemoteDS: WeatherRemoteDS) : IComicsRepository {
    override suspend fun getWeatherDataByCityName (cityName:String): WeatherResponse {
        return weatherRemoteDS.getWeatherDataByCityName(cityName)
    }
}