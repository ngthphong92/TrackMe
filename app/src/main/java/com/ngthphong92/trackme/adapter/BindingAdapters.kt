package com.ngthphong92.trackme.adapter

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.ngthphong92.trackme.R
import com.ngthphong92.trackme.STATE_PAUSE
import com.ngthphong92.trackme.STATE_RECORD


@BindingAdapter("recordState")
fun bindRecordState(view: ImageButton, state: Int) {
    when (state) {
        STATE_RECORD -> {
            view.scaleX = 2f
            view.scaleY = 2.25f
            view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_pause))
        }
        STATE_PAUSE -> {
            view.scaleX = 1.5f
            view.scaleY = 1.5f
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

@BindingAdapter("bind:imageBitmap")
fun loadImage(iv: ImageView, bitmap: Bitmap?) {
    iv.setImageBitmap(bitmap)
}