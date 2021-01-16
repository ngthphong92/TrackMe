package com.ngthphong92.trackme.data.dao

import androidx.room.*
import com.ngthphong92.trackme.data.model.SessionHistory

@Dao
interface SessionHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg obj: SessionHistory)

    @Delete
    suspend fun delete(vararg obj: SessionHistory)

    @Query("SELECT * FROM SessionHistory")
    suspend fun get(): SessionHistory?

    @Query("DELETE FROM SessionHistory")
    suspend fun clear()
}