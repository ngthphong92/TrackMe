package com.ngthphong92.trackme.utils

import androidx.lifecycle.LiveData

class AbsentLiveDataUtils<T> private constructor() : LiveData<T>() {
    companion object {
        @JvmStatic
        fun <T> create(): LiveData<T> {
            return AbsentLiveDataUtils()
        }
    }

    init {
        postValue(null)
    }
}