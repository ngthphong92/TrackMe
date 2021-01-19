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

    fun updateCurrentSession(session: Session?, callbackFunc: (Session?) -> Unit) {
        mSession = session
        mCallbackFunction = callbackFunc
        mLocationUtils.getCurrentLocation()
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

                    if (mSession?.locationList?.isNullOrEmpty() == false) {
                        val fromLoc = mSession?.locationList?.firstOrNull()?.latLng
                        val result = FloatArray(3)
                        Location.distanceBetween(
                            fromLoc?.latitude ?: 0.0, fromLoc?.longitude ?: 0.0,
                            it?.latitude ?: 0.0, it?.longitude ?: 0.0,
                            result
                        )
                        mSession?.apply {
                            this.distance = result[0] / 1000
                            val curTime = Calendar.getInstance().timeInMillis
                            val timeGap = curTime.minus(mSession?.locationList?.firstOrNull()?.time ?: 0).div(HOUR.toFloat())
                            if (this.distance <= 0)
                                return@observe
                            locationList.add(
                                TrackLocation(
                                    latLng = curLocLatLng,
                                    time = curTime,
                                    speed = (this.distance * (1 / timeGap)) / timeGap
                                )
                            )
                        }?.update()
                    } else
                        mSession?.locationList?.add(
                            TrackLocation(
                                latLng = curLocLatLng,
                                time = Calendar.getInstance().timeInMillis
                            )
                        )

                    mCallbackFunction?.invoke(mSession)
                }

                mMap.animateCamera(location)
            }
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

    companion object {
        fun newInstance(): MapsFragment = MapsFragment()
    }
}