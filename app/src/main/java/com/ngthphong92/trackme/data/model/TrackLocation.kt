package com.ngthphong92.trackme.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity
data class TrackLocation(
    @PrimaryKey(autoGenerate = true)
    var locId: Long? = 0L,
    var latLng: LatLng? = null,
    var time: Long = 0L
)