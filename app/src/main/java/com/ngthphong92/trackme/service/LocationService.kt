package com.ngthphong92.trackme.service

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.ngthphong92.trackme.MIN_TIME_METER
import com.ngthphong92.trackme.MIN_TIME_MS
import com.ngthphong92.trackme.TrackMeApplication.Companion.applicationInstance


class LocationService : Service() {

    val locationLiveData = MutableLiveData<Location?>()

    fun getCurrentLocation(callbackService: ((Location) -> Unit)? = null) {
        if (ActivityCompat.checkSelfPermission(
                applicationInstance, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                applicationInstance, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val lm: LocationManager? = applicationInstance.getSystemService(Context.LOCATION_SERVICE) as? LocationManager?
        lm?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            MIN_TIME_MS,
            MIN_TIME_METER,
            object : LocationListener {
                override fun onLocationChanged(loc: Location) {
                    if (callbackService != null)
                        callbackService.invoke(loc)
                    else if (locationLiveData.value != loc)
                        locationLiveData.postValue(loc)
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            })
    }

    override fun onBind(p0: Intent?): IBinder? = null
}