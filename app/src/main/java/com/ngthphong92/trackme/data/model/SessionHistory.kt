package com.ngthphong92.trackme.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SessionHistory(
    @PrimaryKey(autoGenerate = true)
    var historyId: Long? = 0L,
    var sessionList: ArrayList<Session?> = arrayListOf()
)