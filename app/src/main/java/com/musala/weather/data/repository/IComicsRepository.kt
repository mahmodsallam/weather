package com.musala.weather.data.repository

import com.musala.weather.data.remote.WeatherResponse

interface IComicsRepository {
    suspend fun getWeatherDataByCityName(cityName:String): WeatherResponse
}