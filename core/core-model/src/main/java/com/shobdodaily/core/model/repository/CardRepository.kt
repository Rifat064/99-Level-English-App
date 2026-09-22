package com.shobdodaily.core.model.repository

import com.shobdodaily.core.model.Card
import com.shobdodaily.core.model.DailyCardPayload
import kotlinx.coroutines.flow.Flow
import com.shobdodaily.core.model.HistoryCardItem

interface CardRepository {
    fun observeCard(dayIndex: Int): Flow<Result<DailyCardPayload>>
    suspend fun syncTodayCard(): Result<Unit>
    suspend fun syncHistory(currentDayIndex: Int): Result<Unit>
    fun observeHistory(limitDayIndex: Int, currentDayIndex: Int, isSubscriber: Boolean): Flow<List<HistoryCardItem>>
}
