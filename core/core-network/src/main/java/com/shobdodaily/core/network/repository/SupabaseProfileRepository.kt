package com.shobdodaily.core.network.repository

import com.shobdodaily.core.model.repository.ProfileRepository
import com.shobdodaily.core.network.model.ProfileDto
import com.shobdodaily.core.network.model.ProfileInsertDto
import com.shobdodaily.core.network.model.toDomain
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseProfileRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) : ProfileRepository {

    override suspend fun createProfileIfNotExist(userId: String, displayName: String?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val dto = ProfileInsertDto(id = userId, displayName = displayName)
            // Use upsert to handle cases where the profile might already exist, ignoring duplicates
            supabaseClient.postgrest["profiles"].upsert(dto) {
                onConflict = "id"
                ignoreDuplicates = true
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @androidx.annotation.RequiresApi(26)
    override suspend fun getProfile(): Result<com.shobdodaily.core.model.Profile> = withContext(Dispatchers.IO) {
        try {
            val user = supabaseClient.auth.currentUserOrNull()
            if (user == null) {
                return@withContext Result.success(
                    com.shobdodaily.core.model.Profile(
                        id = "guest",
                        displayName = "Guest Learner",
                        enrolledAt = java.time.Instant.now(),
                        wordsPerDay = 2,
                        timezone = "Asia/Dhaka",
                        notifyHour = 8,
                        streakCount = 0,
                        longestStreak = 0,
                        lastCompletedDay = 0
                    )
                )
            }
            
            val dto = supabaseClient.postgrest["profiles"]
                .select {
                    filter {
                        eq("id", user.id)
                    }
                }
                .decodeSingle<ProfileDto>()
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun recordCompletion(currentDayIndex: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = supabaseClient.auth.currentUserOrNull()
                ?: return@withContext Result.failure(Exception("Not authenticated"))

            val dto = supabaseClient.postgrest["profiles"]
                .select {
                    filter {
                        eq("id", user.id)
                    }
                }
                .decodeSingle<ProfileDto>()

            if (dto.lastCompletedDay >= currentDayIndex) {
                // Already completed today or later, do nothing
                return@withContext Result.success(Unit)
            }

            val diff = currentDayIndex - dto.lastCompletedDay
            val newStreakCount = when (diff) {
                1, 2 -> dto.streakCount + 1
                else -> 1
            }

            val newLongestStreak = maxOf(dto.longestStreak, newStreakCount)

            val updateDto = com.shobdodaily.core.network.model.ProfileStreakUpdateDto(
                streakCount = newStreakCount,
                longestStreak = newLongestStreak,
                lastCompletedDay = currentDayIndex
            )

            supabaseClient.postgrest["profiles"]
                .update(updateDto) {
                    filter {
                        eq("id", user.id)
                    }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNotifyHour(hour: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = supabaseClient.auth.currentUserOrNull()
                ?: return@withContext Result.failure(Exception("Not authenticated"))

            supabaseClient.postgrest["profiles"].update(
                mapOf("notify_hour" to hour)
            ) {
                filter {
                    eq("id", user.id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateWordsPerDay(words: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = supabaseClient.auth.currentUserOrNull()
                ?: return@withContext Result.failure(Exception("Not authenticated"))

            supabaseClient.postgrest["profiles"].update(
                mapOf("words_per_day" to words)
            ) {
                filter {
                    eq("id", user.id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
