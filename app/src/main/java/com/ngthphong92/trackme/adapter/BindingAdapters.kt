package com.ngthphong92.trackme.adapter

import android.view.View
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.ngthphong92.trackme.R
import com.ngthphong92.trackme.STATE_PAUSE
import com.ngthphong92.trackme.STATE_RECORDING

@BindingAdapter("recordState")
fun bindRecordState(view: ImageButton, state: Int) {
    view.scaleX = 2f
    when (state) {
        STATE_RECORDING -> {
            view.scaleY = 3f
            view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_pause))
        }
        STATE_PAUSE -> {
            view.scaleY = 2f
            view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_resume))
        }
        else -> view.visibility = View.GONE
    }
}

@BindingAdapter("stopState")
fun bindStopState(view: ImageButton, state: Int) {
    view.scaleX = 2f
    view.scaleY = 2f
    when (state) {
        STATE_PAUSE -> view.visibility = View.VISIBLE
        else -> view.visibility = View.GONE
    }
}
