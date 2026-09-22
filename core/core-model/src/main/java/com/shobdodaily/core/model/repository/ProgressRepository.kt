package com.shobdodaily.core.model.repository

import com.shobdodaily.core.model.UserProgress
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun observeProgress(cardId: Long): Flow<UserProgress?>
    suspend fun markAsLearned(cardId: Long): Result<Unit>
    suspend fun toggleBookmark(cardId: Long): Result<Unit>
    suspend fun syncProgress(): Result<Unit>
    
    // Review Deck
    suspend fun getDueReviewCards(): Result<List<com.shobdodaily.core.model.DailyCardPayload>>
    suspend fun updateSelfRating(cardId: Long, rating: Int): Result<Unit>
}
