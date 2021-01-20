package com.ngthphong92.trackme.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ngthphong92.trackme.DEFAULT_DB
import com.ngthphong92.trackme.data.converter.GenericTypeConverter
import com.ngthphong92.trackme.data.dao.SessionHistoryDao
import com.ngthphong92.trackme.data.model.SessionHistory

@Database(
    entities = [SessionHistory::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    GenericTypeConverter::class
)
abstract class AppDataBase : RoomDatabase() {
    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context, useInMemory: Boolean): AppDataBase =
            INSTANCE ?: synchronized(this) {
                buildDatabase(context, useInMemory).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context, useInMemory: Boolean): AppDataBase {
            val databaseBuilder = if (useInMemory) {
                Room.inMemoryDatabaseBuilder(context, AppDataBase::class.java)
            } else {
                Room.databaseBuilder(context, AppDataBase::class.java, DEFAULT_DB)
            }
            return databaseBuilder
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun sessionHistoryDao(): SessionHistoryDao
}