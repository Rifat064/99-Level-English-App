package com.shobdodaily.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shobdodaily.core.database.model.ProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM user_progress WHERE userId = :userId AND cardId = :cardId")
    fun observeProgress(userId: String, cardId: Long): Flow<ProgressEntity?>
    
    @Query("SELECT * FROM user_progress WHERE userId = :userId AND cardId = :cardId")
    suspend fun getProgress(userId: String, cardId: Long): ProgressEntity?

    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    suspend fun getAllProgressForUser(userId: String): List<ProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: ProgressEntity)
}
