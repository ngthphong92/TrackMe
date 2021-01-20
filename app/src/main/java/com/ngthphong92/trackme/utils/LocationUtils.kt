package com.ngthphong92.trackme.utils

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.ngthphong92.trackme.MIN_TIME_METER
import com.ngthphong92.trackme.MIN_TIME_MS
import com.ngthphong92.trackme.extension.isLocationEnabled
import com.ngthphong92.trackme.ui.REQUEST_SUCCESS
import com.ngthphong92.trackme.ui.activity.TrackMeActivity


class LocationUtils(private val activity: TrackMeActivity) {

    val locationLiveData = MutableLiveData<Location?>()

    fun getCurrentLocation() {
        activity.checkNeededPermission { requestCode ->
            if (requestCode == REQUEST_SUCCESS && activity.isLocationEnabled()) {
                val lm: LocationManager? = activity.getSystemService(Context.LOCATION_SERVICE) as? LocationManager?
                lm?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_MS,
                    MIN_TIME_METER,
                    object : LocationListener {
                        override fun onLocationChanged(loc: Location) {
                            if (locationLiveData.value != loc)
                                locationLiveData.postValue(loc)
                        }

                        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    })
            }
        }
    }
}