package com.shobdodaily.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shobdodaily.core.datastore.SettingsRepository
import com.shobdodaily.core.model.Profile
import com.shobdodaily.core.model.repository.ProfileRepository
import com.shobdodaily.feature.auth.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val profile: Profile? = null,
    val theme: String = "System",
    val ttsAccent: String = "en-US",
    val notifyHour: Int = 8,
    val isSubscriber: Boolean = false, // Placeholder for billing integration
    val isLoading: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
    // private val billingProvider: BillingProvider // P4 implementation
) : ViewModel() {

    private val profileFlow = MutableStateFlow<Profile?>(null)
    private val isLoading = MutableStateFlow(true)

    val uiState: StateFlow<SettingsUiState> = combine(
        profileFlow,
        settingsRepository.theme,
        settingsRepository.ttsAccent,
        settingsRepository.notificationHour,
        isLoading
    ) { profile, theme, accent, notifyHour, loading ->
        SettingsUiState(
            profile = profile,
            theme = theme,
            ttsAccent = accent,
            notifyHour = notifyHour,
            isSubscriber = true, // Temporarily true for free subscriber access testing
            isLoading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            isLoading.value = true
            profileRepository.getProfile().onSuccess { profile ->
                profileFlow.value = profile
                settingsRepository.setNotificationHour(profile.notifyHour)
            }
            isLoading.value = false
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    fun setTtsAccent(accent: String) {
        viewModelScope.launch {
            settingsRepository.setTtsAccent(accent)
        }
    }

    fun setWordsPerDay(words: Int) {
        viewModelScope.launch {
            profileRepository.updateWordsPerDay(words).onSuccess {
                loadProfile()
            }
        }
    }

    fun setNotifyHour(hour: Int) {
        viewModelScope.launch {
            profileRepository.updateNotifyHour(hour).onSuccess {
                settingsRepository.setNotificationHour(hour)
                loadProfile()
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            authRepository.deleteAccount()
        }
    }
}
