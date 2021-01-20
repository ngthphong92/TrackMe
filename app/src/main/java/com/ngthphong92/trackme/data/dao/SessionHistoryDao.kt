package com.ngthphong92.trackme.data.dao

import androidx.room.*
import com.ngthphong92.trackme.data.model.SessionHistory

@Dao
interface SessionHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg obj: SessionHistory?)

    @Delete
    fun delete(vararg obj: SessionHistory)

    @Query("SELECT * FROM SessionHistory")
    fun get(): SessionHistory?

    @Query("DELETE FROM SessionHistory")
    fun clear()
}