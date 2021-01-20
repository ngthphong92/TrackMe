package com.ngthphong92.trackme

import android.app.Application
import androidx.lifecycle.*
import androidx.work.*
import com.ngthphong92.trackme.data.AppDataBase
import com.ngthphong92.trackme.di.viewModelModule
import com.ngthphong92.trackme.extension.readFromSharePref
import com.ngthphong92.trackme.workers.TrackLocationWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TrackMeApplication : Application(), LifecycleObserver {

    private lateinit var trackMekWorkManager: WorkManager

    override fun onCreate() {
        super.onCreate()
        applicationInstance = this
        trackMekWorkManager = WorkManager.getInstance(this)
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
        startWorkerTrackingService()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        startWorkerTrackingService()
    }

    private fun startWorkerTrackingService() {
        val myData: Data = workDataOf(KEY_SESSION_DATA to this.readFromSharePref(KEY_SESSION_DATA_SHARE_PREF))
        val trackWork = OneTimeWorkRequestBuilder<TrackLocationWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setInputData(myData)
        trackMekWorkManager.beginUniqueWork(TRACK_ME_WORK_NAME, ExistingWorkPolicy.REPLACE, trackWork.build()).enqueue()
    }

    companion object {
        @Volatile
        lateinit var applicationInstance: TrackMeApplication
            private set
    }
}