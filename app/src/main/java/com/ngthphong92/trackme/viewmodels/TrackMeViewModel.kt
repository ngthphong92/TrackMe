package com.ngthphong92.trackme.viewmodels

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import com.ngthphong92.trackme.STATE_PAUSE
import com.ngthphong92.trackme.STATE_RECORD
import com.ngthphong92.trackme.STATE_STOP
import com.ngthphong92.trackme.data.model.Session
import com.ngthphong92.trackme.extension.AbsentLiveData.Companion.create

class TrackMeViewModel : ViewModel(), LifecycleObserver {
    private var sessionLiveData = MutableLiveData<Session?>()
    var currentSession = switchMap(sessionLiveData) { data: Session? ->
        if (sessionLiveData.value != null) {
            val liveData = MutableLiveData<Session?>()
            liveData.value = data
            return@switchMap liveData
        }
        create()
    }

    fun startRecord() {
        sessionLiveData.value = sessionLiveData.value?.copy(state = STATE_RECORD) ?: Session(state = STATE_RECORD)
    }

    fun pauseRecord() {
        sessionLiveData.value = sessionLiveData.value?.copy(state = STATE_PAUSE) ?: Session(state = STATE_PAUSE)
    }

    fun stopRecord() {
        sessionLiveData.value = sessionLiveData.value?.copy(state = STATE_STOP) ?: Session(state = STATE_STOP)
    }
}