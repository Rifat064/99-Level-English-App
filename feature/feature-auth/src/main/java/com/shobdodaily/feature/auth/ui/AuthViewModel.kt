package com.shobdodaily.feature.auth.ui

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.shobdodaily.core.model.AnalyticsTracker
import com.shobdodaily.core.model.repository.ProfileRepository
import com.shobdodaily.feature.auth.BuildConfig
import com.shobdodaily.feature.auth.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.shobdodaily.feature.auth.domain.AuthSessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.shobdodaily.core.datastore.SettingsRepository

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    object Success : AuthUiState
    data class Error(val message: String) : AuthUiState
}


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val settingsRepository: SettingsRepository,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun setGuestSession(name: String) {
        viewModelScope.launch {
            settingsRepository.setGuestSession(name, System.currentTimeMillis())
        }
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            authRepository.sessionStatus.collectLatest { status ->
                when (status) {
                    AuthSessionStatus.Authenticated -> {
                        _uiState.value = AuthUiState.Success
                        analyticsTracker.setUserId(authRepository.currentUserId)
                    }
                    AuthSessionStatus.NotAuthenticated -> {
                        if (_uiState.value !is AuthUiState.Error) {
                            _uiState.value = AuthUiState.Idle
                        }
                    }
                    else -> {} // Ignore Loading here, let splash screen handle it if needed
                }
            }
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context, request)
                val credential = result.credential

                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    
                    val authResult = authRepository.signInWithGoogle(idToken)
                    if (authResult.isSuccess) {
                        val userId = authRepository.currentUserId
                        if (userId != null) {
                            val photoUrl = googleIdTokenCredential.profilePictureUri?.toString()
                            profileRepository.createProfileIfNotExist(userId, googleIdTokenCredential.displayName, photoUrl)
                        }
                        _uiState.value = AuthUiState.Success
                    } else {
                        _uiState.value = AuthUiState.Error(
                            authResult.exceptionOrNull()?.message ?: "Auth failed"
                        )
                    }
                } else {
                    _uiState.value = AuthUiState.Error("Unexpected credential type")
                }
            } catch (e: GetCredentialException) {
                _uiState.value = AuthUiState.Error("Sign in failed: ${e.message}")
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("An error occurred: ${e.localizedMessage}")
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.signInAnonymously()
            if (result.isSuccess) {
                val userId = authRepository.currentUserId
                if (userId != null) {
                    profileRepository.createProfileIfNotExist(userId, null, null)
                }
                _uiState.value = AuthUiState.Success
            } else {
                _uiState.value = AuthUiState.Error(
                    result.exceptionOrNull()?.message ?: "Anonymous auth failed"
                )
            }
        }
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val credentialManager = CredentialManager.create(context)
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
                authRepository.signOut()
                // The flow collector in checkAuthStatus will handle setting state to Idle
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Sign out failed: ${e.message}")
            }
        }
    }

    fun deleteAccount(context: Context) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val credentialManager = CredentialManager.create(context)
                val result = authRepository.deleteAccount()
                if (result.isSuccess) {
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                    // The flow collector in checkAuthStatus will handle setting state to Idle
                } else {
                    _uiState.value = AuthUiState.Error(
                        result.exceptionOrNull()?.message ?: "Delete account failed"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Delete account failed: ${e.message}")
            }
        }
    }
}
