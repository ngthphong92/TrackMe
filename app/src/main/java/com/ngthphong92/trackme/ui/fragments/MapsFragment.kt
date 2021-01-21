package com.ngthphong92.trackme.ui.fragments

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
import com.ngthphong92.trackme.CUR_LOCATION_RADIUS
import com.ngthphong92.trackme.MAP_ZOOM_LEVEL_NEAR
import com.ngthphong92.trackme.R
import com.ngthphong92.trackme.STATE_RECORD
import com.ngthphong92.trackme.data.model.Session
import com.ngthphong92.trackme.data.model.TrackLatLng
import com.ngthphong92.trackme.databinding.FragmentMapsBinding
import com.ngthphong92.trackme.ui.BaseFragment

class MapsFragment : BaseFragment() {

    private var mBinding: FragmentMapsBinding? = null
    private lateinit var mMap: GoogleMap
    private var mPolylineOption: PolylineOptions? = null
    private var mFromMarker: Marker? = null
    private var mToMarker: Circle? = null
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
        googleMap?.uiSettings?.isCompassEnabled = false
        googleMap?.uiSettings?.isScrollGesturesEnabled = false
        googleMap?.uiSettings?.isZoomControlsEnabled = false
        googleMap?.uiSettings?.setAllGesturesEnabled(false)
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
        mapFragment?.getMapAsync(callback)
        bindEvent()
    }

    private fun bindEvent() {
        if (trackMeActivity?.trackLocService?.sessionLiveData?.hasObservers() == false)
            trackMeActivity?.trackLocService?.sessionLiveData?.observe(viewLifecycleOwner) {
                if (mSession?.state == STATE_RECORD) {
                    val lastLocLatLng = it?.locationList?.lastOrNull()?.latLng
                    val curLocLatLng = LatLng(lastLocLatLng?.latitude ?: 0.0, lastLocLatLng?.longitude ?: 0.0)
                    val location = CameraUpdateFactory.newLatLngZoom(curLocLatLng, MAP_ZOOM_LEVEL_NEAR)
                    renderMap(TrackLatLng(latitude = curLocLatLng.latitude, longitude = curLocLatLng.longitude))
                    mCallbackFunction?.invoke(mSession)
                    mMap.animateCamera(location)
                }
            }
    }

    private fun renderMap(curLocationLatLng: TrackLatLng) {
        if (mFromMarker == null)
            mFromMarker = mMap.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            (curLocationLatLng).latitude ?: 0.0,
                            (curLocationLatLng).longitude ?: 0.0
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
        mPolylineOption?.add(LatLng(curLocationLatLng.latitude ?: 0.0, curLocationLatLng.longitude ?: 0.0))
        mMap.addPolyline(mPolylineOption)
    }

    companion object {
        fun newInstance() = MapsFragment()
    }
}