package com.ngthphong92.trackme

import android.app.Application
import androidx.lifecycle.*
import com.ngthphong92.trackme.data.AppDataBase
import com.ngthphong92.trackme.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TrackMeApplication : Application(), LifecycleObserver {

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
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
    }

    companion object {
        @Volatile
        lateinit var applicationInstance: TrackMeApplication
            private set
    }
}