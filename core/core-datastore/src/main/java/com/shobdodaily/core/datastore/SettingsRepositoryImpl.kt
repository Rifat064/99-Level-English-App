package com.shobdodaily.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_ONBOARDING_COMPLETED] ?: false
    }

    override val notificationHour: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NOTIFICATION_HOUR] ?: 8
    }

    override suspend fun completeOnboarding() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_ONBOARDING_COMPLETED] = true
        }
    }

    override suspend fun setNotificationHour(hour: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_HOUR] = hour
        }
    }

    private object PreferencesKeys {
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
    }
}
