package com.ngthphong92.trackme.service

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.ngthphong92.trackme.*
import com.ngthphong92.trackme.TrackMeApplication.Companion.applicationInstance
import com.ngthphong92.trackme.data.model.Session
import com.ngthphong92.trackme.data.model.TrackLatLng
import com.ngthphong92.trackme.data.model.TrackLocation
import java.util.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

class LocationService : Service() {

    private val binder = LocalBinder()
    var curSession: Session? = null
    val sessionLiveData = MutableLiveData<Session?>()
    private var prevLoc: Location? = null

    fun getCurrentLocation() {
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
                    if (prevLoc != loc || prevLoc == null) {
                        sessionLiveData.postValue(recordLocation(loc))
                        prevLoc = loc
                    }
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            })
    }

    private fun recordLocation(it: Location?): Session? {
        val curLocLatLng = LatLng(it?.latitude ?: 0.0, it?.longitude ?: 0.0)
        if (curSession?.locationList?.isNullOrEmpty() == false) {
            val firstLoc = curSession?.locationList?.firstOrNull()
            val lastLoc = curSession?.locationList?.lastOrNull()
            val firstLocLatLng = firstLoc?.latLng
            val lastLocLatLng = lastLoc?.latLng
            val distance = calculateDistance(
                firstLocLatLng?.latitude ?: 0.0, firstLocLatLng?.longitude ?: 0.0,
                it?.latitude ?: 0.0, it?.longitude ?: 0.0,
            )
            val requiredDistance = calculateDistance(
                lastLocLatLng?.latitude ?: 0.0, lastLocLatLng?.longitude ?: 0.0,
                it?.latitude ?: 0.0, it?.longitude ?: 0.0,
            )
            curSession?.apply {
                if (requiredDistance > 0.01) {
                    this.distance = floor(distance.toFloat() * 100) / 100
                    val curTime = Calendar.getInstance().timeInMillis
                    val timeGap = (curTime.minus(firstLoc?.time ?: 0)).div(HOUR.toFloat())
                    val speed = floor((this.distance / timeGap) * 100) / 100
                    locationList.add(
                        TrackLocation(
                            latLng = TrackLatLng(latitude = curLocLatLng.latitude, longitude = curLocLatLng.longitude),
                            time = curTime, speed = speed
                        )
                    )
                    update()
                }
            }
        } else
            curSession?.locationList?.add(
                TrackLocation(
                    latLng = TrackLatLng(
                        latitude = curLocLatLng.latitude,
                        longitude = curLocLatLng.longitude
                    ), time = Calendar.getInstance().timeInMillis
                )
            )

        return curSession
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist =
            (sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + (cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return dist / 0.62137
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    override fun onBind(intent: Intent?): IBinder = binder

    inner class LocalBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }
}