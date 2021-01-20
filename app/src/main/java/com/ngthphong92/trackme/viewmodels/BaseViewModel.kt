package com.ngthphong92.trackme.viewmodels

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.ngthphong92.trackme.TrackMeApplication.Companion.applicationInstance
import com.ngthphong92.trackme.data.AppDataBase

open class BaseViewModel : ViewModel(), LifecycleObserver {
    protected var database: AppDataBase = applicationInstance.getDatabaseInstant()
}