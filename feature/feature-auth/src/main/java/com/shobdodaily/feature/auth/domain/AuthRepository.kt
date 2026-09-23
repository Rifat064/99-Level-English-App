package com.shobdodaily.feature.auth.domain

import kotlinx.coroutines.flow.StateFlow

enum class AuthSessionStatus {
    Loading,
    Authenticated,
    NotAuthenticated
}

interface AuthRepository {
    val sessionStatus: StateFlow<AuthSessionStatus>
    val currentUserId: String?
    
    suspend fun signInWithGoogle(idToken: String): Result<Unit>
    suspend fun signInAnonymously(): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    fun isUserSignedIn(): Boolean
}
