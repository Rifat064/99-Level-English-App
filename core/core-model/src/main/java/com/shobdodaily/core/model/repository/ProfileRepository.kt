package com.shobdodaily.core.model.repository

interface ProfileRepository {
    suspend fun createProfileIfNotExist(userId: String, displayName: String?): Result<Unit>
}
