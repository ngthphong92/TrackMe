package com.ngthphong92.trackme.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.ngthphong92.trackme.*
import com.ngthphong92.trackme.data.model.Session
import com.ngthphong92.trackme.data.model.SessionHistory
import com.ngthphong92.trackme.utils.AbsentLiveDataUtils.Companion.create
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackMeViewModel : BaseViewModel() {
    private var mSessionLiveData = MutableLiveData<Session?>()
    var sessionHistory = MutableLiveData<SessionHistory?>()
    var currentSession = switchMap(mSessionLiveData) { data: Session? ->
        if (mSessionLiveData.value != null) {
            val liveData = MutableLiveData<Session?>()
            liveData.value = data
            return@switchMap liveData
        }
        create()
    }

    fun startRecord() {
        mSessionLiveData.value = mSessionLiveData.value?.copy(state = STATE_RECORD) ?: Session(state = STATE_RECORD)
    }

    fun pauseRecord() {
        mSessionLiveData.value = mSessionLiveData.value?.copy(state = STATE_PAUSE) ?: Session(state = STATE_PAUSE)
    }

    fun stopRecord() {
        mSessionLiveData.value = mSessionLiveData.value?.copy(state = STATE_STOP) ?: Session(state = STATE_STOP)
    }

    fun getSessionHistory() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.runCatching { database.sessionHistoryDao().get() }
                    .onSuccess {
                        sessionHistory.postValue(it ?: SessionHistory())
                    }
            }
        }
    }

    fun saveSessionHistory(session: Session? = null) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.runCatching { database.sessionHistoryDao().get() }
                    .onSuccess {
                        val history = it ?: SessionHistory()
                        history.historyList.add(session ?: currentSession.value)
                        sessionHistory.postValue(history)
                        database.sessionHistoryDao().insert(history)
                    }
            }
        }
    }
}