package com.ngthphong92.trackme

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.*
import com.ngthphong92.trackme.di.viewModelModule
import com.ngthphong92.trackme.workers.TrackLocationWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class TrackMeApplication : Application() {

    private var appLifecycleObserver = AppLifecycleObserver()
    private lateinit var trackMekWorkManager: WorkManager

    override fun onCreate() {
        super.onCreate()
        instance = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
        trackMekWorkManager = WorkManager.getInstance(this)
        startKoin {
            androidLogger()
            androidContext(this@TrackMeApplication)
            koin.loadModules(listOf(viewModelModule))
            koin.createRootScope()
        }
    }

    inner class AppLifecycleObserver : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onEnterForeground() {
            trackMekWorkManager.beginUniqueWork(
                TRACK_ME_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(TrackLocationWorker::class.java)
            ).enqueue()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onEnterBackground() {
            val periodicWorkRequest =
                PeriodicWorkRequest.Builder(TrackLocationWorker::class.java, PERIODIC_INTERVAL, TimeUnit.MINUTES)
                    .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                    .build()
            trackMekWorkManager.enqueue(periodicWorkRequest)
        }
    }

    companion object {
        @Volatile
        lateinit var instance: TrackMeApplication
            private set
    }
}