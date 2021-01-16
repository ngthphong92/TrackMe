package com.ngthphong92.trackme

import android.app.Application
import com.ngthphong92.trackme.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TrackMeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidLogger()
            androidContext(this@TrackMeApplication)
            koin.loadModules(listOf(viewModelModule))
            koin.createRootScope()
        }
    }

    companion object {
        @Volatile
        lateinit var instance: TrackMeApplication
            private set
    }
}