package com.ngthphong92.trackme.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ngthphong92.trackme.MAP_ZOOM_LEVEL
import com.ngthphong92.trackme.R
import com.ngthphong92.trackme.databinding.FragmentMapsBinding
import com.ngthphong92.trackme.ui.BaseFragment
import com.ngthphong92.trackme.utils.LocationUtils

class MapsFragment : BaseFragment() {

    private lateinit var mMap: GoogleMap
    private var mBinding: FragmentMapsBinding? = null
    private lateinit var mLocationUtils: LocationUtils

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
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
                val location = CameraUpdateFactory.newLatLngZoom(LatLng(it?.latitude ?: 0.0, it?.longitude ?: 0.0), MAP_ZOOM_LEVEL)
                mMap.animateCamera(location)
                mLocationUtils.locationLiveData.removeObservers(viewLifecycleOwner)
            }
    }

    companion object {
        fun newInstance(): MapsFragment = MapsFragment()
    }
}