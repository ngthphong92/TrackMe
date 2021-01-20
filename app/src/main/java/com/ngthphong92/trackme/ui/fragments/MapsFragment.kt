package com.ngthphong92.trackme.ui.fragments

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.ngthphong92.trackme.*
import com.ngthphong92.trackme.data.model.Session
import com.ngthphong92.trackme.data.model.TrackLocation
import com.ngthphong92.trackme.databinding.FragmentMapsBinding
import com.ngthphong92.trackme.ui.BaseFragment
import com.ngthphong92.trackme.utils.LocationUtils
import java.util.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

class MapsFragment : BaseFragment() {

    private var mBinding: FragmentMapsBinding? = null
    private lateinit var mMap: GoogleMap
    private lateinit var mLocationUtils: LocationUtils
    private var mPolylineOption: PolylineOptions? = null
    private var mFromMarker: Marker? = null
    private var mToMarker: Circle? = null
    private var mSession: Session? = null
    private var mCallbackFunction: ((Session?) -> Unit)? = null

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        val hcmCity = LatLng(10.8231, 106.6297)

        googleMap?.uiSettings?.isCompassEnabled = true
        googleMap?.uiSettings?.isMapToolbarEnabled = false
        googleMap?.uiSettings?.isScrollGesturesEnabled = true
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.uiSettings?.setAllGesturesEnabled(true)

        mPolylineOption = PolylineOptions().apply {
            width(5f)
            visible(true)
            color(ContextCompat.getColor(requireContext(), R.color.stop_poly_line))
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(hcmCity))
    }

    fun startUpdateCurrentSession(session: Session?, callbackFunc: (Session?) -> Unit) {
        mSession = session
        mCallbackFunction = callbackFunc
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentMapsBinding.inflate(inflater, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.fcvMaps) as? SupportMapFragment?
        mLocationUtils = LocationUtils(trackMeActivity!!)
        mLocationUtils.getCurrentLocation()
        mapFragment?.getMapAsync(callback)
        bindEvent()
    }

    private fun bindEvent() {
        if (!mLocationUtils.locationLiveData.hasObservers())
            mLocationUtils.locationLiveData.observe(viewLifecycleOwner) {
                val curLocLatLng = LatLng(it?.latitude ?: 0.0, it?.longitude ?: 0.0)
                val location = CameraUpdateFactory.newLatLngZoom(curLocLatLng, MAP_ZOOM_LEVEL)

                if (mSession?.state == STATE_RECORD) {
                    renderMap(curLocLatLng)
                    recordLocation(it, curLocLatLng)
                    mCallbackFunction?.invoke(mSession)
                }

                mMap.animateCamera(location)
            }
    }

    private fun recordLocation(it: Location?, curLocLatLng: LatLng) {
        if (mSession?.locationList?.isNullOrEmpty() == false) {
            val firstLoc = mSession?.locationList?.firstOrNull()
            val lastLoc = mSession?.locationList?.lastOrNull()
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
            mSession?.apply {
                if (requiredDistance > 0.01) {
                    this.distance = floor(distance.toFloat() * 100) / 100
                    val curTime = Calendar.getInstance().timeInMillis
                    val timeGap = (curTime.minus(firstLoc?.time ?: 0)).div(HOUR.toFloat())
                    val speed = floor((this.distance / timeGap) * 100) / 100
                    locationList.add(
                        TrackLocation(latLng = curLocLatLng, time = curTime, speed = speed)
                    )
                    update()
                }
            }
        } else
            mSession?.locationList?.add(
                TrackLocation(latLng = curLocLatLng, time = Calendar.getInstance().timeInMillis)
            )
    }

    private fun renderMap(curLocationLatLng: LatLng) {
        if (mFromMarker == null)
            mFromMarker = mMap.addMarker(
                MarkerOptions()
                    .position(curLocationLatLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
            )
        mToMarker?.remove()
        mToMarker = mMap.addCircle(
            CircleOptions()
                .center(curLocationLatLng)
                .radius(CUR_LOCATION_RADIUS)
                .strokeColor(ContextCompat.getColor(requireContext(), R.color.current_location_out))
                .fillColor(ContextCompat.getColor(requireContext(), R.color.current_location_in))
        )
        mPolylineOption?.add(curLocationLatLng)
        mMap.addPolyline(mPolylineOption)
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

    companion object {
        fun newInstance(): MapsFragment = MapsFragment()
    }
}