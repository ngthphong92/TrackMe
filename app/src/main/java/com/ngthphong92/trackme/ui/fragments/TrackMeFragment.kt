package com.ngthphong92.trackme.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ngthphong92.trackme.R
import com.ngthphong92.trackme.databinding.FragmentMapsBinding
import com.ngthphong92.trackme.databinding.FragmentTrackMeBinding
import com.ngthphong92.trackme.ui.BaseFragment

class TrackMeFragment : BaseFragment<FragmentTrackMeBinding>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBinding = FragmentTrackMeBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapsActivity?.activityBinding?.fbRecord?.visibility = View.GONE
    }
}