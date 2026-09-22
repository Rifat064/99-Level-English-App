package com.shobdodaily.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shobdodaily.core.database.model.QuizAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizAttemptDao {
    @Query("SELECT * FROM quiz_attempts WHERE userId = :userId AND weekIndex = :weekIndex")
    fun observeQuizAttempt(userId: String, weekIndex: Int): Flow<QuizAttemptEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAttempt(attempt: QuizAttemptEntity)
}
