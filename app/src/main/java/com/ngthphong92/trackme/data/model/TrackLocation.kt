package com.ngthphong92.trackme.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose

@Entity
data class TrackLocation(
    @PrimaryKey(autoGenerate = true)
    var locId: Long? = 0L,
    @Expose
    var latLng: LatLng? = null,
    @Expose
    var time: Long = 0L,
    @Expose
    var speed: Float = 0f
)