package com.ngthphong92.trackme.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ngthphong92.trackme.ui.activity.TrackMeActivity

open class BaseFragment : Fragment() {

    var trackMeActivity: TrackMeActivity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackMeActivity = activity as? TrackMeActivity?
        trackMeActivity?.activityBinding?.ablMaps?.visibility = View.VISIBLE
    }
}