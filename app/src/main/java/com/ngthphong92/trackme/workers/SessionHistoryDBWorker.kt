package com.ngthphong92.trackme.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.ngthphong92.trackme.HISTORY_SESSION_FILENAME
import com.ngthphong92.trackme.data.AppDataBase
import com.ngthphong92.trackme.data.model.SessionHistory
import kotlinx.coroutines.coroutineScope

class SessionHistoryDBWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        try {
            applicationContext.assets.open(HISTORY_SESSION_FILENAME).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val sessionHistory: SessionHistory? =
                        Gson().fromJson(jsonReader, SessionHistory::class.java)
                    val database = AppDataBase.getInstance(applicationContext, false)
                    database.sessionHistoryDao().insert(sessionHistory ?: SessionHistory())

                    Result.success()
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error inserting database", ex)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "SessionHistoryDBWorker"
    }
}
