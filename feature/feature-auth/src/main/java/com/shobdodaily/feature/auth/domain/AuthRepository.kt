package com.shobdodaily.feature.auth.domain

import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val sessionStatus: StateFlow<SessionStatus>
    val currentUserId: String?
    
    suspend fun signInWithGoogle(idToken: String): Result<Unit>
    suspend fun signInAnonymously(): Result<Unit>
    suspend fun signOut(): Result<Unit>
    fun isUserSignedIn(): Boolean
}
