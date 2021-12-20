package com.musala.weather.presentation

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musala.weather.data.remote.NetworkingViewState
import com.musala.weather.data.repository.ComicsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(private val moviesRepository: ComicsRepository) :
    ViewModel() {
    private var _weatherData = MutableLiveData<NetworkingViewState>()
    val weatherData  = _weatherData
    private var searchJob: Job? = null
    fun getWeatherByCityName(cityName:String) {
        viewModelScope.launch(Dispatchers.IO){
            try{
                _weatherData.postValue(NetworkingViewState.Loading)
                val result =   moviesRepository.getWeatherDataByCityName(cityName)
                _weatherData.postValue(NetworkingViewState.Success(result))
            }
            catch (e:Exception){
                _weatherData.postValue(NetworkingViewState.Error(e))
                Log.d("Exception" , e.message)

            }
        }
    }
    fun performSearch(cityName: String){
//        viewModelScope.launch {
//            cityName.debounce(3000)
//                .distinctUntilChanged()
//                .mapLatest {
//                    getWeatherByCityName(it)
//                }
//                .flowOn(Dispatchers.IO)
//                .collect()
//
//
//        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            getWeatherByCityName(cityName)
        }

    }
}