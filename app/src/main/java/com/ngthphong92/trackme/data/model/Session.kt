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
    var from: TrackLocation = TrackLocation(),
    @Expose
    var to: TrackLocation = TrackLocation(),
    @Expose
    var distance: Float = 0f,
    @Expose
    var duration: Long = 0L,
    @Expose
    var averageSpeed: Long = 0L,
    @Expose
    var state: Int = STATE_STOP
) {
    fun getCurrentDuration(): String {
        return duration.toFormat()
    }
}