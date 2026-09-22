package com.shobdodaily.core.datastore

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isOnboardingCompleted: Flow<Boolean>
    val notificationHour: Flow<Int>
    val theme: Flow<String>
    val ttsAccent: Flow<String>
    
    suspend fun completeOnboarding()
    suspend fun setNotificationHour(hour: Int)
    suspend fun setTheme(theme: String)
    suspend fun setTtsAccent(accent: String)
}
