package com.shobdodaily.app.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shobdodaily.core.datastore.SettingsRepository
import com.shobdodaily.feature.auth.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.shobdodaily.feature.auth.domain.AuthSessionStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface SplashUiState {
    object Loading : SplashUiState
    object GoToLogin : SplashUiState
    object GoToOnboarding : SplashUiState
    object GoToHome : SplashUiState
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    authRepository: AuthRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SplashUiState> = combine(
        authRepository.sessionStatus,
        settingsRepository.isOnboardingCompleted
    ) { sessionStatus, isOnboardingCompleted ->
        when (sessionStatus) {
            AuthSessionStatus.Authenticated -> {
                if (isOnboardingCompleted) {
                    SplashUiState.GoToHome
                } else {
                    SplashUiState.GoToOnboarding
                }
            }
            AuthSessionStatus.NotAuthenticated -> SplashUiState.GoToLogin
            else -> SplashUiState.Loading // Initializing or RefreshFailure (could retry, but keep loading until resolved)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SplashUiState.Loading
    )
}
