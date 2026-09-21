package com.shobdodaily.feature.auth.domain

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): Result<Unit>
    suspend fun signInAnonymously(): Result<Unit>
    suspend fun signOut(): Result<Unit>
    fun isUserSignedIn(): Boolean
}
