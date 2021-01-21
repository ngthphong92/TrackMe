package com.ngthphong92.trackme.adapter

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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

@BindingAdapter("duration")
fun TextView.setTime(duration: Long) {
    val diffSeconds = duration / 1000 % 60
    val diffMinutes = duration / (60 * 1000) % 60
    val diffHours = duration / (60 * 60 * 1000) % 23
    text = String.format("%02d:%02d:%02d", diffHours, diffMinutes, diffSeconds)
}