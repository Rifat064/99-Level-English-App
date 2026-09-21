package com.shobdodaily.core.model.repository

import com.shobdodaily.core.model.Card
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    fun observeCard(dayIndex: Int): Flow<Result<Card>>
    suspend fun syncTodayCard(): Result<Unit>
}
