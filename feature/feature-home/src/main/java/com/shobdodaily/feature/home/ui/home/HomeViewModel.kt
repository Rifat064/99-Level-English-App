package com.shobdodaily.feature.home.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shobdodaily.core.model.Profile
import com.shobdodaily.core.model.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import com.shobdodaily.core.model.repository.NotificationScheduler

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val profile: Profile,
        val currentDayIndex: Int,
        val isTodayCompleted: Boolean
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val result = profileRepository.getProfile()
            result.onSuccess { profile ->
                // Calculate current day index based on enrolledAt
                val zoneId = ZoneId.of(profile.timezone)
                val enrolledDate = profile.enrolledAt.atZone(zoneId).toLocalDate()
                val todayDate = Instant.now().atZone(zoneId).toLocalDate()
                
                val daysDifference = ChronoUnit.DAYS.between(enrolledDate, todayDate).toInt()
                // Day index is 1-based. If enrolled today, dayIndex is 1
                val currentDayIndex = maxOf(1, daysDifference + 1)
                
                val isTodayCompleted = profile.lastCompletedDay >= currentDayIndex

                // Schedule or update daily notification based on profile setting
                notificationScheduler.scheduleDailyNotification(profile.notifyHour)

                _uiState.value = HomeUiState.Success(
                    profile = profile,
                    currentDayIndex = currentDayIndex,
                    isTodayCompleted = isTodayCompleted
                )
            }.onFailure { exception ->
                _uiState.value = HomeUiState.Error(exception.message ?: "Failed to load profile")
            }
        }
    }
}
