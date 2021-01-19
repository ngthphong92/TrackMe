package com.ngthphong92.trackme.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.ngthphong92.trackme.STATE_STOP
import com.ngthphong92.trackme.extension.toFormat

@Entity
data class Session(
    @PrimaryKey(autoGenerate = true)
    var sessionId: Long? = 0L,
    @Expose
    var locationList: ArrayList<TrackLocation> = arrayListOf(),
    @Expose
    var distance: Float = 0f,
    @Expose
    var duration: Long = 0L,
    @Expose
    var averageSpeed: Float = 0f,
    @Expose
    var state: Int = STATE_STOP
) {
    fun update() {
        duration = locationList.lastOrNull()?.time?.minus(locationList.firstOrNull()?.time ?: 0) ?: 0
        averageSpeed = locationList.map { it.speed }.average().toFloat()
    }

    fun getDurationStr(): String {
        return duration.toFormat()
    }

    fun getSpeed(): Float {
        return if (state == STATE_STOP) {
            averageSpeed
        } else
            locationList.lastOrNull()?.speed ?: 0f
    }
}