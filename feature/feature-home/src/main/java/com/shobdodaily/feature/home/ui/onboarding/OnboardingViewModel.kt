package com.shobdodaily.feature.home.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shobdodaily.core.datastore.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val notificationHour: StateFlow<Int> = settingsRepository.notificationHour
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 8
        )

    fun setNotificationHour(hour: Int) {
        viewModelScope.launch {
            settingsRepository.setNotificationHour(hour)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            settingsRepository.completeOnboarding()
        }
    }
}
