package com.ngthphong92.trackme.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ngthphong92.trackme.databinding.FragmentSessionHistoryBinding
import com.ngthphong92.trackme.ui.BaseFragment
import com.ngthphong92.trackme.ui.activity.MapsActivity

class SessionHistoryFragment : BaseFragment<FragmentSessionHistoryBinding>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBinding = FragmentSessionHistoryBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapsActivity?.activityBinding?.fbRecord?.visibility = View.VISIBLE
        mapsActivity?.activityBinding?.fbRecord?.setOnClickListener {
            val direction = SessionHistoryFragmentDirections.actionSessionHistoryFragmentToMapsFragment()
            findNavController().navigate(direction)
        }
    }
}