package com.ngthphong92.trackme.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.databinding.DataBindingUtil
import com.ngthphong92.trackme.KEY_SESSION_DATA_SHARE_PREF
import com.ngthphong92.trackme.R
import com.ngthphong92.trackme.databinding.ActivityMapBinding
import com.ngthphong92.trackme.extension.removeFromSharePref
import com.ngthphong92.trackme.service.LocationService
import com.ngthphong92.trackme.ui.BaseActivity


class TrackMeActivity : BaseActivity() {

    var activityBinding: ActivityMapBinding? = null
    lateinit var trackLocService: LocationService
    var mBound: Boolean = false
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            trackLocService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        removeFromSharePref(KEY_SESSION_DATA_SHARE_PREF)
    }

    override fun onStart() {
        super.onStart()
        Intent(this, LocationService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }
}