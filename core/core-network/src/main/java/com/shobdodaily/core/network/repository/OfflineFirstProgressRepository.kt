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
    private val progressDao: ProgressDao
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
}
