package com.ngthphong92.trackme.utils

import android.location.Location
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.ngthphong92.trackme.extension.isLocationEnabled
import com.ngthphong92.trackme.ui.BaseActivity
import com.ngthphong92.trackme.ui.REQUEST_SUCCESS
import com.ngthphong92.trackme.ui.activity.TrackMeActivity

class LocationUtils(private val activity: TrackMeActivity) {

    val locationLiveData = MutableLiveData<Location?>()

    private var mFusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationLiveData.postValue(locationResult.lastLocation)
        }
    }

    fun getCurrentLocation() {
        activity.checkNeededPermission { requestCode ->
            if (requestCode == REQUEST_SUCCESS && activity.isLocationEnabled()) {
                mFusedLocationClient.lastLocation?.addOnCompleteListener { task: Task<Location?> ->
                    val location = task.result
                    if (location == null) {
                        val mLocationRequest = LocationRequest()
                        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        mLocationRequest.interval = 0
                        mLocationRequest.fastestInterval = 0
                        mLocationRequest.numUpdates = 1
                        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
                        mFusedLocationClient.requestLocationUpdates(
                            mLocationRequest, mLocationCallback,
                            Looper.myLooper()
                        )
                    } else {
                        if (locationLiveData.value != location)
                            locationLiveData.postValue(location)
                    }
                }
            }
        }
    }
}