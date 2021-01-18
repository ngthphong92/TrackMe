package com.ngthphong92.trackme

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.*
import com.ngthphong92.trackme.di.viewModelModule
import com.ngthphong92.trackme.extension.readFromSharePref
import com.ngthphong92.trackme.workers.TrackLocationWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class TrackMeApplication : Application(), LifecycleObserver {

    private lateinit var trackMekWorkManager: WorkManager

    override fun onCreate() {
        super.onCreate()
        instance = this
        trackMekWorkManager = WorkManager.getInstance(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        startKoin {
            androidLogger()
            androidContext(this@TrackMeApplication)
            koin.loadModules(listOf(viewModelModule))
            koin.createRootScope()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val myData: Data = workDataOf(KEY_SESSION_DATA to this.readFromSharePref(KEY_SESSION_DATA_SHARE_PREF))
        val trackWork = OneTimeWorkRequestBuilder<TrackLocationWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setInputData(myData)
        trackMekWorkManager.beginUniqueWork(TRACK_ME_WORK_NAME, ExistingWorkPolicy.REPLACE, trackWork.build()).enqueue()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val myData: Data = workDataOf(KEY_SESSION_DATA to this.readFromSharePref(KEY_SESSION_DATA_SHARE_PREF))
        val trackWork = PeriodicWorkRequest.Builder(TrackLocationWorker::class.java, PERIODIC_INTERVAL, TimeUnit.MINUTES)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setInputData(myData)
            .build()
        trackMekWorkManager.enqueue(trackWork)
    }

    companion object {
        @Volatile
        lateinit var instance: TrackMeApplication
            private set
    }
}