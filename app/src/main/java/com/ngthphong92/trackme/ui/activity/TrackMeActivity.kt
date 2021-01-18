package com.ngthphong92.trackme.ui.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.ngthphong92.trackme.KEY_SESSION_DATA_SHARE_PREF
import com.ngthphong92.trackme.R
import com.ngthphong92.trackme.databinding.ActivityMapBinding
import com.ngthphong92.trackme.extension.removeFromSharePref
import com.ngthphong92.trackme.ui.BaseActivity


class TrackMeActivity : BaseActivity() {

    var activityBinding: ActivityMapBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        removeFromSharePref(KEY_SESSION_DATA_SHARE_PREF)
    }
}