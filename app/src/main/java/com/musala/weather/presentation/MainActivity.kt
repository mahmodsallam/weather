package com.musala.weather.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.musala.weather.R
import com.musala.weather.data.remote.NetworkingViewState
import com.musala.weather.data.remote.WeatherResponse
import com.musala.weather.databinding.ActivityMainBinding
import com.musala.weather.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusCheck()
        getLocationPermission()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel.weatherData.observe(this, Observer {
            populateUI(it)
        })
        binding.etSearch.doAfterTextChanged {
            mainViewModel.performSearch(it.toString())
        }
        
    }

    private fun populateUI(it: NetworkingViewState) {
        when (it) {
            is NetworkingViewState.Loading -> {showLoading(true)}
            is NetworkingViewState.Error -> {showLoading(false)
                showToast(getString(R.string.city_not_found))
            }
            is NetworkingViewState.Success<*> -> {
                showLoading(false)
                var item = it.item as WeatherResponse
                with(binding) {
                    tvWeatherStatusName.text = item.weather.get(0).description
                    tvTemp.text = ((item.main.temp).minus(273.15)).toInt()
                        .toString() + getString(R.string.celisus)
                    tvCityName.text = item.name
                    Glide.with(this@MainActivity)
                        .load("${Constants.IMG_URL}${item.weather[0].icon}${Constants.IMG_SIZE}")
                        .into(ivWeatherIcon)
                }
            }
        }

    }

    private fun statusCheck() {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.enable_gps))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.yes)
            ) { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

            }
            .setNegativeButton(
                getString(R.string.no)
            ) { dialog, _ -> dialog.cancel() }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            var fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient?.lastLocation?.addOnSuccessListener {
                if (it != null) {
                    var geoCoder = Geocoder(this, Locale.ENGLISH)
                    var address = geoCoder.getFromLocation(it.latitude, it.longitude, 1)
                    var cityName = address?.get(0)?.subAdminArea
                    if (cityName != null) {
                        mainViewModel.getWeatherByCityName(cityName)

                    }

                }
            }

        }
    }

    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
            )
            return
        } else {
            getCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        }

    }

    override fun onPause() {
        isPaused = true
        super.onPause()
    }
    override fun onResume() {
        if (isPaused) getCurrentLocation()
        isPaused = false
        super.onResume()
    }
    private fun showLoading(show:Boolean){
        if (show) binding.loadingBar.visibility = View.VISIBLE else binding.loadingBar.visibility = View.GONE
    }
    private fun showToast(text:String){Toast.makeText(this , text , Toast.LENGTH_SHORT).show()}

}