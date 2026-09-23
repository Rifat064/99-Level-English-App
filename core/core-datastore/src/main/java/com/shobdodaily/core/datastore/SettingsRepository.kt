package com.shobdodaily.core.datastore

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isOnboardingCompleted: Flow<Boolean>
    val notificationHour: Flow<Int>
    val theme: Flow<String>
    val ttsAccent: Flow<String>
    
    val guestName: Flow<String?>
    val isEasterEggUnlocked: Flow<Boolean>

    val guestLoginTimestamp: Flow<Long?>
    
    suspend fun completeOnboarding()
    suspend fun setNotificationHour(hour: Int)
    suspend fun setTheme(theme: String)
    suspend fun setTtsAccent(accent: String)
    suspend fun setGuestSession(name: String, timestamp: Long)
    suspend fun clearGuestSession()
    suspend fun setEasterEggUnlocked(unlocked: Boolean)
}
