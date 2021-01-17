package com.ngthphong92.trackme.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ngthphong92.trackme.R
import com.ngthphong92.trackme.STATE_PAUSE
import com.ngthphong92.trackme.STATE_STOP
import com.ngthphong92.trackme.databinding.FragmentTrackMeBinding
import com.ngthphong92.trackme.ui.BaseFragment
import com.ngthphong92.trackme.viewmodels.TrackMeViewModel

class TrackMeFragment : BaseFragment<FragmentTrackMeBinding>() {

    private val mTrackMeViewModel: TrackMeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBinding = FragmentTrackMeBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackMeActivity?.activityBinding?.fbRecord?.visibility = View.GONE
        trackMeActivity?.activityBinding?.mtMaps?.title = getString(R.string.session_track_me)
        bindInterfaceEvent()
        bindEvent()
    }

    private fun bindInterfaceEvent() {
        fragmentBinding?.ibRecord?.setOnClickListener {
            if (mTrackMeViewModel.currentSession.value?.state == STATE_PAUSE)
                mTrackMeViewModel.startRecord()
            else
                mTrackMeViewModel.pauseRecord()
        }
        fragmentBinding?.ibStop?.setOnClickListener {
            mTrackMeViewModel.stopRecord()
        }
    }

    private fun bindEvent() {
        if (!mTrackMeViewModel.currentSession.hasObservers())
            mTrackMeViewModel.currentSession.observe(viewLifecycleOwner) {
                fragmentBinding?.session = it
                when (it?.state) {
                    STATE_STOP -> {
                        findNavController().navigateUp()
                        mTrackMeViewModel.currentSession.removeObservers(viewLifecycleOwner)
                    }
                }
            }
    }
}