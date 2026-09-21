package com.shobdodaily.core.datastore

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isOnboardingCompleted: Flow<Boolean>
    val notificationHour: Flow<Int>
    
    suspend fun completeOnboarding()
    suspend fun setNotificationHour(hour: Int)
}
