package com.shobdodaily.core.network.repository

import com.shobdodaily.core.database.dao.ProgressDao
import com.shobdodaily.core.model.UserProgress
import com.shobdodaily.core.model.repository.ProgressRepository
import com.shobdodaily.core.network.model.UserProgressDto
import com.shobdodaily.core.network.model.toDomain
import com.shobdodaily.core.network.model.toDto
import com.shobdodaily.core.network.model.toEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineFirstProgressRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val progressDao: ProgressDao,
    private val cardDao: com.shobdodaily.core.database.dao.CardDao,
    private val wordDao: com.shobdodaily.core.database.dao.WordDao
) : ProgressRepository {

    override fun observeProgress(cardId: Long): Flow<UserProgress?> {
        val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return kotlinx.coroutines.flow.emptyFlow()
        return progressDao.observeProgress(userId, cardId).map { it?.toDomain() }
    }

    override suspend fun markAsLearned(cardId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return@withContext Result.failure(Exception("Not logged in"))
            val existing = progressDao.getProgress(userId, cardId)
            
            val newEntity = existing?.copy(completed = true) ?: com.shobdodaily.core.database.model.ProgressEntity(
                userId = userId,
                cardId = cardId,
                seenAt = Instant.now().toString(),
                completed = true,
                bookmarked = false,
                selfRating = null
            )
            
            progressDao.upsertProgress(newEntity)
            
            // Sync to supabase
            supabaseClient.postgrest["user_progress"].upsert(newEntity.toDto())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleBookmark(cardId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return@withContext Result.failure(Exception("Not logged in"))
            val existing = progressDao.getProgress(userId, cardId)
            val isBookmarked = existing?.bookmarked ?: false
            
            val newEntity = existing?.copy(bookmarked = !isBookmarked) ?: com.shobdodaily.core.database.model.ProgressEntity(
                userId = userId,
                cardId = cardId,
                seenAt = Instant.now().toString(),
                completed = false,
                bookmarked = !isBookmarked,
                selfRating = null
            )
            
            progressDao.upsertProgress(newEntity)
            
            // Sync to supabase
            supabaseClient.postgrest["user_progress"].upsert(newEntity.toDto())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncProgress(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return@withContext Result.success(Unit)
            val remoteProgress = supabaseClient.postgrest["user_progress"]
                .select()
                .decodeList<UserProgressDto>()
                
            remoteProgress.forEach { dto ->
                progressDao.upsertProgress(dto.toEntity())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDueReviewCards(): Result<List<com.shobdodaily.core.model.DailyCardPayload>> = withContext(Dispatchers.IO) {
        try {
            val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return@withContext Result.failure(Exception("Not logged in"))
            
            // 1. Get all progress for user
            val allProgress = progressDao.getAllProgressForUser(userId).map { it.toDomain() }
            
            // 2. Determine due cards
            val dueProgress = com.shobdodaily.core.model.ReviewScheduler.getDueCards(allProgress, Instant.now())
            
            // 3. Fetch the full cards for those due
            val dueCardIds = dueProgress.map { it.cardId }
            val payloads = dueCardIds.mapNotNull { cardId ->
                val cardEntity = cardDao.getCardById(cardId) ?: return@mapNotNull null
                val wordAEntity = wordDao.getWordById(cardEntity.wordAId ?: 0)
                val wordBEntity = wordDao.getWordById(cardEntity.wordBId ?: 0)
                
                if (wordAEntity != null && wordBEntity != null) {
                    val card = com.shobdodaily.core.model.Card(
                        id = cardEntity.id,
                        dayIndex = cardEntity.dayIndex,
                        season = cardEntity.season,
                        wordAId = cardEntity.wordAId ?: 0,
                        wordBId = cardEntity.wordBId ?: 0,
                        sentenceEn = cardEntity.sentenceEn,
                        sentenceBn = cardEntity.sentenceBn,
                        bubbleText = cardEntity.bubbleText,
                        imagePath = cardEntity.imagePath,
                        imageBlurhash = cardEntity.imageBlurhash,
                        status = cardEntity.status
                    )
                    
                    val wordA = com.shobdodaily.core.model.Word(
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
                    
                    val wordB = com.shobdodaily.core.model.Word(
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
                    
                    com.shobdodaily.core.model.DailyCardPayload(cardEntity.dayIndex, card, wordA, wordB)
                } else {
                    null
                }
            }
            
            Result.success(payloads)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSelfRating(cardId: Long, rating: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = supabaseClient.auth.currentUserOrNull()?.id ?: return@withContext Result.failure(Exception("Not logged in"))
            val existing = progressDao.getProgress(userId, cardId)
            
            val newEntity = existing?.copy(selfRating = rating, seenAt = Instant.now().toString()) 
                ?: com.shobdodaily.core.database.model.ProgressEntity(
                    userId = userId,
                    cardId = cardId,
                    seenAt = Instant.now().toString(),
                    completed = true,
                    bookmarked = false,
                    selfRating = rating
                )
            
            progressDao.upsertProgress(newEntity)
            
            // Sync to supabase
            supabaseClient.postgrest["user_progress"].upsert(newEntity.toDto())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
