package com.ngthphong92.trackme.extension

import androidx.lifecycle.LiveData

class AbsentLiveData<T> private constructor() : LiveData<T>() {
    companion object {
        @JvmStatic
        fun <T> create(): LiveData<T> {
            return AbsentLiveData()
        }
    }

    init {
        postValue(null)
    }
}