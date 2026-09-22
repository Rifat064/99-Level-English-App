package com.shobdodaily.core.model.repository

interface ProfileRepository {
    suspend fun createProfileIfNotExist(userId: String, displayName: String?): Result<Unit>
    suspend fun getProfile(): Result<com.shobdodaily.core.model.Profile>
    suspend fun recordCompletion(currentDayIndex: Int): Result<Unit>
}
