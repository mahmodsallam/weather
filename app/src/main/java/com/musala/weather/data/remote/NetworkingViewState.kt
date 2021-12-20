package com.musala.weather.data.remote

sealed class NetworkingViewState {
    object Loading : NetworkingViewState()
    class Success<T>(val item: T) : NetworkingViewState()
    class Error(val error: Throwable) : NetworkingViewState()
}