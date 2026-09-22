package com.shobdodaily.core.model.repository

import com.shobdodaily.core.model.UserProgress
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun observeProgress(cardId: Long): Flow<UserProgress?>
    suspend fun markAsLearned(cardId: Long): Result<Unit>
    suspend fun toggleBookmark(cardId: Long): Result<Unit>
    suspend fun syncProgress(): Result<Unit>
}
