package com.ngthphong92.trackme.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity
data class SessionHistory(
    @PrimaryKey
    var historyId: Long? = 0L,
    @Expose
    var sessionList: ArrayList<Session?> = arrayListOf()
)