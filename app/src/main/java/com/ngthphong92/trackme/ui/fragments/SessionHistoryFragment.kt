package com.ngthphong92.trackme.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.ngthphong92.trackme.CUR_LOCATION_RADIUS
import com.ngthphong92.trackme.MAP_ZOOM_LEVEL_FAR
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
                ItemSessionHistoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
            },
            bindFunc = { binding, _, item, _ ->
                if (binding is ItemSessionHistoryBinding) {
                    renderMap(binding, item)
                    binding.session = item
                }
            },
            diffUtilCallback = {
                BaseDiffUtilCallback(areContentsTheSameFunc = { oldItem, newItem -> oldItem?.sessionId == newItem?.sessionId })
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
        mBinding?.rvSessionHistory?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && trackMeActivity?.activityBinding?.fbRecord?.isShown == true) {
                    trackMeActivity?.activityBinding?.fbRecord?.hide()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    trackMeActivity?.activityBinding?.fbRecord?.show()
                }
            }
        })
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
                if (!it?.sessionList.isNullOrEmpty()) {
                    it?.sessionList?.toPagerList<Any>(trackMeActivity?.lifecycleScope) { data ->
                        CoroutineScope(Job() + Dispatchers.Main).launch {
                            mHistoryAdapter?.submitData(data)
                        }
                    }
                }
            }
    }

    private fun renderMap(binding: ItemSessionHistoryBinding, item: Session?) {
        var map: GoogleMap
        binding.mvMaps.onCreate(null)
        binding.mvMaps.onResume()
        binding.mvMaps.getMapAsync { googleMap ->
            val fromLoc = item?.locationList?.firstOrNull()
            val toLoc = item?.locationList?.lastOrNull()
            MapsInitializer.initialize(binding.root.context)
            map = googleMap
            map.uiSettings?.apply {
                isCompassEnabled = false
                isMapToolbarEnabled = false
                isScrollGesturesEnabled = false
                isZoomControlsEnabled = false
                setAllGesturesEnabled(false)
            }
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(fromLoc?.latLng?.latitude ?: 0.0, fromLoc?.latLng?.longitude ?: 0.0))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            map.addCircle(
                CircleOptions()
                    .center(LatLng(toLoc?.latLng?.latitude ?: 0.0, toLoc?.latLng?.longitude ?: 0.0))
                    .radius(CUR_LOCATION_RADIUS)
                    .strokeColor(ContextCompat.getColor(requireContext(), R.color.current_location_out))
                    .fillColor(ContextCompat.getColor(requireContext(), R.color.current_location_in))
            )
            val polylineOption = PolylineOptions().apply {
                width(5f)
                visible(true)
                color(ContextCompat.getColor(requireContext(), R.color.stop_poly_line))
            }
            polylineOption.add(LatLng(fromLoc?.latLng?.latitude ?: 0.0, fromLoc?.latLng?.longitude ?: 0.0))
            polylineOption.add(LatLng(toLoc?.latLng?.latitude ?: 0.0, toLoc?.latLng?.longitude ?: 0.0))
            map.addPolyline(polylineOption)
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(toLoc?.latLng?.latitude ?: 0.0, toLoc?.latLng?.longitude ?: 0.0), MAP_ZOOM_LEVEL_FAR
                )
            )
        }
    }
}