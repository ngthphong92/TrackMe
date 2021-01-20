package com.ngthphong92.trackme.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ngthphong92.trackme.KEY_SESSION_DATA
import com.ngthphong92.trackme.STATE_RECORD
import com.ngthphong92.trackme.data.converter.customGson
import com.ngthphong92.trackme.data.model.Session
import com.ngthphong92.trackme.service.LocationService
import com.ngthphong92.trackme.utils.makeStatusNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class TrackLocationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private lateinit var mLocationService: LocationService
    override suspend fun doWork(): Result {
        return coroutineScope {
            try {
                val session = customGson.fromJson(inputData.getString(KEY_SESSION_DATA), Session::class.java)
                if (session?.state == STATE_RECORD) {
                    delay(1000)
                    makeStatusNotification("Recording location", applicationContext)
                    mLocationService = LocationService()
                    withContext(Dispatchers.Main) {
                        mLocationService.getCurrentLocation {
                            //Todo handle recording feature when app in the background
                        }
                    }
                }
                Result.success()
            } catch (ex: Exception) {
                Log.e(TAG, "Error", ex)
                Result.failure()
            }
        }
    }

    companion object {
        private const val TAG = "TrackLocationWorker"
    }
}
