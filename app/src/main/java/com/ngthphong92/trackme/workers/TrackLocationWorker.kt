package com.ngthphong92.trackme.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class TrackLocationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        try {
            delay(5000)
            makeStatusNotification("Recording location", applicationContext)
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
