package com.ngthphong92.trackme.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ngthphong92.trackme.KEY_SESSION_DATA
import com.ngthphong92.trackme.STATE_RECORD
import com.ngthphong92.trackme.data.converter.customGson
import com.ngthphong92.trackme.data.model.Session
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class TrackLocationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        try {
            //Todo check again for non-null value data
            val session = customGson.fromJson(inputData.getString(KEY_SESSION_DATA), Session::class.java)
            if (session?.state == STATE_RECORD) {
                delay(5000)
                makeStatusNotification("Recording location", applicationContext)
            }
            Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, "Error", ex)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "TrackLocationWorker"
    }
}
