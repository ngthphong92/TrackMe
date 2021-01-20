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
import com.ngthphong92.trackme.data.model.TrackLatLng
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
    private var mFromLoc: TrackLocation? = null
    private var mToLoc: TrackLocation? = null
    private var mSession: Session? = null
    private var mCallbackFunction: ((Session?) -> Unit)? = null

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        mPolylineOption = PolylineOptions().apply {
            width(5f)
            visible(true)
            color(ContextCompat.getColor(requireContext(), R.color.stop_poly_line))
        }

        googleMap?.uiSettings?.isMapToolbarEnabled = false
        if (mToLoc?.latLng != null && mFromLoc?.latLng != null) {
            googleMap?.uiSettings?.isCompassEnabled = false
            googleMap?.uiSettings?.isScrollGesturesEnabled = false
            googleMap?.uiSettings?.isZoomControlsEnabled = false
            googleMap?.uiSettings?.setAllGesturesEnabled(false)
            renderMap(
                mToLoc?.latLng ?: return@OnMapReadyCallback,
                mFromLoc?.latLng ?: return@OnMapReadyCallback
            )
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(mToLoc?.latLng?.latitude ?: 0.0, mToLoc?.latLng?.longitude ?: 0.0), MAP_ZOOM_LEVEL_FAR
                )
            )
        } else {
            googleMap?.uiSettings?.isCompassEnabled = true
            googleMap?.uiSettings?.isScrollGesturesEnabled = true
            googleMap?.uiSettings?.isZoomControlsEnabled = true
            googleMap?.uiSettings?.setAllGesturesEnabled(true)
            val hcmCity = LatLng(mToLoc?.latLng?.latitude ?: 10.8231, mToLoc?.latLng?.longitude ?: 106.6297)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hcmCity, MAP_ZOOM_LEVEL_NEAR))
        }
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
                if (mSession?.state == STATE_RECORD) {
                    val curLocLatLng = LatLng(it?.latitude ?: 0.0, it?.longitude ?: 0.0)
                    val location = CameraUpdateFactory.newLatLngZoom(curLocLatLng, MAP_ZOOM_LEVEL_NEAR)
                    renderMap(TrackLatLng(latitude = curLocLatLng.latitude, longitude = curLocLatLng.longitude))
                    recordLocation(it, curLocLatLng)
                    mCallbackFunction?.invoke(mSession)
                    mMap.animateCamera(location)
                }
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
                        TrackLocation(
                            latLng = TrackLatLng(latitude = curLocLatLng.latitude, longitude = curLocLatLng.longitude),
                            time = curTime, speed = speed
                        )
                    )
                    update()
                }
            }
        } else
            mSession?.locationList?.add(
                TrackLocation(
                    latLng = TrackLatLng(
                        latitude = curLocLatLng.latitude,
                        longitude = curLocLatLng.longitude
                    ), time = Calendar.getInstance().timeInMillis
                )
            )
    }

    private fun renderMap(curLocationLatLng: TrackLatLng, fromLocationLatLng: TrackLatLng? = null) {
        if (mFromMarker == null)
            mFromMarker = mMap.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            (fromLocationLatLng ?: curLocationLatLng).latitude ?: 0.0,
                            (fromLocationLatLng ?: curLocationLatLng).longitude ?: 0.0
                        )
                    )
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
        mToMarker?.remove()
        mToMarker = mMap.addCircle(
            CircleOptions()
                .center(LatLng(curLocationLatLng.latitude ?: 0.0, curLocationLatLng.longitude ?: 0.0))
                .radius(CUR_LOCATION_RADIUS)
                .strokeColor(ContextCompat.getColor(requireContext(), R.color.current_location_out))
                .fillColor(ContextCompat.getColor(requireContext(), R.color.current_location_in))
        )

        if (mFromLoc != null && mFromLoc != null)
            mPolylineOption?.add(LatLng(mFromLoc?.latLng?.latitude ?: 0.0, mFromLoc?.latLng?.longitude ?: 0.0))
        mPolylineOption?.add(LatLng(curLocationLatLng.latitude ?: 0.0, curLocationLatLng.longitude ?: 0.0))

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
        fun newInstance(fromLoc: TrackLocation? = null, toLoc: TrackLocation? = null): MapsFragment {
            val mapsFragment = MapsFragment()
            mapsFragment.mFromLoc = fromLoc
            mapsFragment.mToLoc = toLoc
            return mapsFragment
        }
    }
}