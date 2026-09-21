package com.shobdodaily.core.network.repository

import com.shobdodaily.core.model.repository.ProfileRepository
import com.shobdodaily.core.network.model.ProfileInsertDto
import io.github.jan.supabase.SupabaseClient
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
}
