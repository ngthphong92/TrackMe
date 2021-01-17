package com.ngthphong92.trackme.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ngthphong92.trackme.R
import com.ngthphong92.trackme.STATE_RECORD
import com.ngthphong92.trackme.databinding.FragmentSessionHistoryBinding
import com.ngthphong92.trackme.ui.BaseFragment
import com.ngthphong92.trackme.ui.REQUEST_SUCCESS
import com.ngthphong92.trackme.viewmodels.TrackMeViewModel

class SessionHistoryFragment : BaseFragment<FragmentSessionHistoryBinding>() {

    private val mTrackMeViewModel: TrackMeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBinding = FragmentSessionHistoryBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackMeActivity?.activityBinding?.fbRecord?.visibility = View.VISIBLE
        trackMeActivity?.activityBinding?.mtMaps?.title = getString(R.string.session_history_title)
        bindInterfaceEvent()
        bindEvent()
    }

    private fun bindInterfaceEvent() {
        trackMeActivity?.activityBinding?.fbRecord?.setOnClickListener {
            trackMeActivity?.checkNeededPermission { requestCode ->
                if (requestCode == REQUEST_SUCCESS)
                    mTrackMeViewModel.startRecord()
            }
        }
    }

    private fun bindEvent() {
        if (!mTrackMeViewModel.currentSession.hasObservers())
            mTrackMeViewModel.currentSession.observe(viewLifecycleOwner) {
                if (it?.state == STATE_RECORD) {
                    val direction = SessionHistoryFragmentDirections.actionSessionHistoryFragmentToTrackMeFragment()
                    findNavController().navigate(direction)
                    mTrackMeViewModel.currentSession.removeObservers(viewLifecycleOwner)
                }
            }
    }
}