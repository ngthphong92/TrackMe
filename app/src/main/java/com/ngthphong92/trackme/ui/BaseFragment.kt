package com.ngthphong92.trackme.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ngthphong92.trackme.ui.activity.MapsActivity

open class BaseFragment<T> : Fragment() {

    var fragmentBinding: T? = null
    var mapsActivity: MapsActivity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapsActivity = activity as? MapsActivity?
        mapsActivity?.activityBinding?.ablMaps?.visibility = View.VISIBLE
        mapsActivity?.activityBinding?.mtMaps?.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}