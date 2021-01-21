package com.ngthphong92.trackme

import android.app.Application
import androidx.lifecycle.*
import com.ngthphong92.trackme.data.AppDataBase
import com.ngthphong92.trackme.di.viewModelModule
import com.ngthphong92.trackme.utils.makeStatusNotification
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TrackMeApplication : Application(), LifecycleObserver {

    var wasInBackground: Boolean = false

    override fun onCreate() {
        super.onCreate()
        applicationInstance = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        startKoin {
            androidLogger()
            androidContext(this@TrackMeApplication)
            koin.loadModules(listOf(viewModelModule))
            koin.createRootScope()
        }
    }

    fun getDatabaseInstant(useInMemoryDb: Boolean = false): AppDataBase {
        return AppDataBase.getInstance(this, useInMemoryDb)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        wasInBackground = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        makeStatusNotification(TRACK_ME_START_NOTIFICATION, this)
        wasInBackground = true
    }

    companion object {
        @Volatile
        lateinit var applicationInstance: TrackMeApplication
            private set
    }
}