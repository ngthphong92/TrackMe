package com.ngthphong92.trackme.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ngthphong92.trackme.STATE_STOP
import com.ngthphong92.trackme.extension.toFormat

@Entity
data class Session(
    @PrimaryKey(autoGenerate = true)
    var sessionId: Long? = 0L,
    var from: TrackLocation = TrackLocation(),
    var to: TrackLocation = TrackLocation(),
    var distance: Float = 0f,
    var duration: Long = 0L,
    var averageSpeed: Long = 0L,
    var state: Int = STATE_STOP
) {
    fun getCurrentDuration(): String {
        return duration.toFormat()
    }
}