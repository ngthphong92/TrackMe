package com.ngthphong92.trackme.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ngthphong92.trackme.R
import com.ngthphong92.trackme.STATE_RECORD
import com.ngthphong92.trackme.adapter.BaseDiffUtilCallback
import com.ngthphong92.trackme.adapter.BasePagedListAdapter
import com.ngthphong92.trackme.data.model.Session
import com.ngthphong92.trackme.databinding.FragmentSessionHistoryBinding
import com.ngthphong92.trackme.databinding.ItemSessionHistoryBinding
import com.ngthphong92.trackme.ui.BaseFragment
import com.ngthphong92.trackme.ui.REQUEST_SUCCESS
import com.ngthphong92.trackme.utils.toPagerList
import com.ngthphong92.trackme.viewmodels.TrackMeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SessionHistoryFragment : BaseFragment() {

    private val mTrackMeViewModel: TrackMeViewModel by activityViewModels()
    private var mBinding: FragmentSessionHistoryBinding? = null
    private var mHistoryAdapter: BasePagedListAdapter<Session?>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentSessionHistoryBinding.inflate(inflater, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackMeActivity?.activityBinding?.fbRecord?.visibility = View.VISIBLE
        trackMeActivity?.activityBinding?.mtMaps?.title = getString(R.string.session_history_title)
        mHistoryAdapter = BasePagedListAdapter(
            onCreateViewHolderFunc = { viewGroup, _ ->
                ItemSessionHistoryBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
            },
            bindFunc = { binding, _, item, _ ->
                if (binding is ItemSessionHistoryBinding) {
                    binding.session = item
                }
            },
            diffUtilCallback = {
                BaseDiffUtilCallback(
                    areContentsTheSameFunc = { oldItem, newItem ->
                        oldItem?.sessionId == newItem?.sessionId
                    }
                )
            }
        )
        mBinding?.rvSessionHistory?.adapter = mHistoryAdapter
        mTrackMeViewModel.getSessionHistory()
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
                    val direction =
                        SessionHistoryFragmentDirections.actionSessionHistoryFragmentToTrackMeFragment()
                    findNavController().navigate(direction)
                    mTrackMeViewModel.currentSession.removeObservers(viewLifecycleOwner)
                }
            }
        if (!mTrackMeViewModel.sessionHistory.hasObservers())
            mTrackMeViewModel.sessionHistory.observe(viewLifecycleOwner) {
                if (!it?.historyList.isNullOrEmpty()) {
                    it?.historyList?.toPagerList<Any>(trackMeActivity?.lifecycleScope) { data ->
                        CoroutineScope(Job() + Dispatchers.Main).launch {
                            mHistoryAdapter?.submitData(data)
                        }
                    }
                }
            }
    }
}