package com.ngthphong92.trackme.viewmodels

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.ngthphong92.trackme.*
import com.ngthphong92.trackme.data.converter.customGson
import com.ngthphong92.trackme.data.model.Session
import com.ngthphong92.trackme.utils.AbsentLiveDataUtils.Companion.create
import com.ngthphong92.trackme.workers.TrackLocationWorker

class TrackMeViewModel : ViewModel(), LifecycleObserver {
    private var mWorkManager: WorkManager = WorkManager.getInstance(TrackMeApplication.instance)
    private var mSessionLiveData = MutableLiveData<Session?>()

    var currentSession = switchMap(mSessionLiveData) { data: Session? ->
        if (mSessionLiveData.value != null) {
            val liveData = MutableLiveData<Session?>()
            liveData.value = data
            return@switchMap liveData
        }
        create()
    }

    internal fun startRecord() {
        mSessionLiveData.value = mSessionLiveData.value?.copy(state = STATE_RECORD) ?: Session(state = STATE_RECORD)
        applyTrack()
    }

    internal fun pauseRecord() {
        mSessionLiveData.value = mSessionLiveData.value?.copy(state = STATE_PAUSE) ?: Session(state = STATE_PAUSE)
        applyTrack()
    }

    internal fun stopRecord() {
        mSessionLiveData.value = mSessionLiveData.value?.copy(state = STATE_STOP) ?: Session(state = STATE_STOP)
        applyTrack()
    }

    private fun applyTrack() {
        val sessionBuilder = storeSessionDataBuilder()
        mWorkManager.beginUniqueWork(
            TRACK_ME_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.from(TrackLocationWorker::class.java)
        )
            .then(sessionBuilder.build())
            .enqueue()
    }

    private fun storeSessionDataBuilder(): OneTimeWorkRequest.Builder {
        val builder = Data.Builder()
        builder.putString(KEY_SESSION_DATA, customGson.toJson(mSessionLiveData.value))
        val blurBuilder = OneTimeWorkRequestBuilder<TrackLocationWorker>()
        blurBuilder.setInputData(builder.build())
        return blurBuilder
    }
}