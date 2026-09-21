package com.shobdodaily.core.network.repository

import com.shobdodaily.core.database.dao.CardDao
import com.shobdodaily.core.database.dao.WordDao
import com.shobdodaily.core.model.Card
import com.shobdodaily.core.model.repository.CardRepository
import com.shobdodaily.core.network.model.DailyCardPayloadDto
import com.shobdodaily.core.network.model.toEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json

@Singleton
class OfflineFirstCardRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val cardDao: CardDao,
    private val wordDao: WordDao
) : CardRepository {

    override fun observeCard(dayIndex: Int): Flow<Result<Card>> {
        return cardDao.observeCardByDayIndex(dayIndex).map { entity ->
            if (entity != null) {
                // Map Entity back to Domain (need to add toDomain in Entity or map it here)
                Result.success(
                    Card(
                        id = entity.id,
                        dayIndex = entity.dayIndex,
                        season = entity.season,
                        wordAId = entity.wordAId ?: 0,
                        wordBId = entity.wordBId ?: 0,
                        sentenceEn = entity.sentenceEn,
                        sentenceBn = entity.sentenceBn,
                        bubbleText = entity.bubbleText,
                        imagePath = entity.imagePath,
                        imageBlurhash = entity.imageBlurhash,
                        status = entity.status
                    )
                )
            } else {
                Result.failure(Exception("Card not found in local database for day \$dayIndex"))
            }
        }
    }

    override suspend fun syncTodayCard(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Fetch from edge function
            val response = supabaseClient.functions.invoke("get-daily-card")
            val payloadDto = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }.decodeFromString<DailyCardPayloadDto>(response.body<String>())
            
            // Map to entities
            val cardEntity = payloadDto.card.toEntity()
            val wordAEntity = payloadDto.wordA.toEntity()
            val wordBEntity = payloadDto.wordB.toEntity()

            // Insert words first (foreign key constraints if any)
            wordDao.insertWords(wordAEntity, wordBEntity)
            // Insert card
            cardDao.insertCards(cardEntity)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
