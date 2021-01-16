package com.ngthphong92.trackme.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Session(
    @PrimaryKey(autoGenerate = true)
    var sessionId: Long? = 0L,
    var from: TrackLocation = TrackLocation(),
    var to: TrackLocation = TrackLocation(),
    var distance: Long = 0L,
    var duration: Long = 0L,
    var averageSpeed: Long = 0L
)