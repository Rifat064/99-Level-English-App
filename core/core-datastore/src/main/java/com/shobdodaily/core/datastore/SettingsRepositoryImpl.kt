package com.shobdodaily.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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

    override val theme: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.THEME] ?: "System"
    }

    override val ttsAccent: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TTS_ACCENT] ?: "en-US"
    }

    override val guestName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.GUEST_NAME]
    }

    override val isEasterEggUnlocked: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_EASTER_EGG_UNLOCKED] ?: false
    }


    override val guestLoginTimestamp: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.GUEST_LOGIN_TIMESTAMP]
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

    override suspend fun setTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme
        }
    }

    override suspend fun setTtsAccent(accent: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TTS_ACCENT] = accent
        }
    }

    override suspend fun setGuestSession(name: String, timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GUEST_NAME] = name
            preferences[PreferencesKeys.GUEST_LOGIN_TIMESTAMP] = timestamp
        }
    }

    override suspend fun clearGuestSession() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.GUEST_NAME)
            preferences.remove(PreferencesKeys.GUEST_LOGIN_TIMESTAMP)
        }
    }

    override suspend fun setEasterEggUnlocked(unlocked: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_EASTER_EGG_UNLOCKED] = unlocked
        }
    }

    private object PreferencesKeys {
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        val THEME = stringPreferencesKey("theme")
        val TTS_ACCENT = stringPreferencesKey("tts_accent")
        val GUEST_NAME = stringPreferencesKey("guest_name")
        val GUEST_LOGIN_TIMESTAMP = longPreferencesKey("guest_login_timestamp")
        val IS_EASTER_EGG_UNLOCKED = booleanPreferencesKey("is_easter_egg_unlocked")
    }
}
