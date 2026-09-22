package com.shobdodaily.core.network.repository

import com.shobdodaily.core.database.dao.CardDao
import com.shobdodaily.core.database.dao.WordDao
import com.shobdodaily.core.model.Card
import com.shobdodaily.core.model.repository.CardRepository
import com.shobdodaily.core.network.model.DailyCardPayloadDto
import com.shobdodaily.core.network.model.toEntity
import com.shobdodaily.core.model.DailyCardPayload
import com.shobdodaily.core.model.Word
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import kotlinx.coroutines.Dispatchers
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import com.shobdodaily.core.model.HistoryCardItem
import com.shobdodaily.core.network.model.CardDto
import com.shobdodaily.core.network.model.toEntity

@Singleton
class OfflineFirstCardRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val cardDao: CardDao,
    private val wordDao: WordDao
) : CardRepository {

    override fun observeCard(dayIndex: Int): Flow<Result<DailyCardPayload>> {
        return cardDao.observeCardByDayIndex(dayIndex).map { entity ->
            if (entity != null) {
                val wordAEntity = wordDao.getWordById(entity.wordAId ?: 0)
                val wordBEntity = wordDao.getWordById(entity.wordBId ?: 0)
                
                if (wordAEntity != null && wordBEntity != null) {
                    val card = Card(
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
                    
                    val wordA = Word(
                        id = wordAEntity.id,
                        word = wordAEntity.word,
                        pos = wordAEntity.pos,
                        bangla = wordAEntity.bangla,
                        englishGloss = wordAEntity.englishGloss,
                        examTag = wordAEntity.examTag,
                        examCategory = wordAEntity.examCategory,
                        difficulty = wordAEntity.difficulty,
                        frequencyRank = wordAEntity.frequencyRank
                    )
                    
                    val wordB = Word(
                        id = wordBEntity.id,
                        word = wordBEntity.word,
                        pos = wordBEntity.pos,
                        bangla = wordBEntity.bangla,
                        englishGloss = wordBEntity.englishGloss,
                        examTag = wordBEntity.examTag,
                        examCategory = wordBEntity.examCategory,
                        difficulty = wordBEntity.difficulty,
                        frequencyRank = wordBEntity.frequencyRank
                    )
                    
                    Result.success(DailyCardPayload(dayIndex, card, wordA, wordB))
                } else {
                    Result.failure(Exception("Words not found for card \$dayIndex"))
                }
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

    override suspend fun syncHistory(currentDayIndex: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val cardsDto = supabaseClient.postgrest["cards"]
                .select() {
                    filter {
                        lte("day_index", currentDayIndex)
                    }
                }
                .decodeList<CardDto>()

            val cardEntities = cardsDto.map { it.toEntity() }
            cardDao.insertCards(*cardEntities.toTypedArray())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeHistory(limitDayIndex: Int, currentDayIndex: Int, isSubscriber: Boolean): Flow<List<HistoryCardItem>> {
        return cardDao.observeHistoryCards(limitDayIndex).map { entities ->
            entities.map { entity ->
                val dayIndex = entity.card.dayIndex
                val isCompleted = entity.progress?.completed ?: false
                
                // Entitlement logic:
                // Free window: Last 7 days, including today (currentDayIndex - 7 to currentDayIndex)
                // If not free, it's locked unless they are a subscriber
                val isFree = dayIndex > (currentDayIndex - 7)
                val isLocked = if (isSubscriber) false else !isFree

                HistoryCardItem(
                    cardId = entity.card.id,
                    dayIndex = dayIndex,
                    season = entity.card.season,
                    imageBlurhash = entity.card.imageBlurhash,
                    isCompleted = isCompleted,
                    isLocked = isLocked
                )
            }
        }
    }
}
