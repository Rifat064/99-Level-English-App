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

import com.shobdodaily.core.datastore.SettingsRepository
import kotlinx.coroutines.flow.first

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val profile: Profile,
        val guestName: String?,
        val currentDayIndex: Int,
        val isTodayCompleted: Boolean,
        val missedDaysCount: Int = 0,
        val catchUpDayIndex: Int? = null,
        val missedDaysLost: Int = 0 // Days lost because they fell outside the free window
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val notificationScheduler: NotificationScheduler,
    private val settingsRepository: SettingsRepository
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

                // Catch-up logic
                val totalMissed = maxOf(0, currentDayIndex - profile.lastCompletedDay - 1)
                val isSubscriber = false // TODO: integrate BillingProvider for P4
                val freeWindowDays = 7
                val oldestFreeDay = maxOf(1, currentDayIndex - freeWindowDays + 1)
                
                val catchUpDayIndex = if (totalMissed > 0) {
                    if (isSubscriber) {
                        profile.lastCompletedDay + 1
                    } else {
                        maxOf(profile.lastCompletedDay + 1, oldestFreeDay)
                    }
                } else {
                    null
                }
                
                val missedDaysLost = if (!isSubscriber && totalMissed > 0) {
                    maxOf(0, oldestFreeDay - (profile.lastCompletedDay + 1))
                } else {
                    0
                }
                
                val catchUpDaysAvailable = if (catchUpDayIndex != null) {
                    currentDayIndex - catchUpDayIndex
                } else {
                    0
                }

                // Schedule or update daily notification based on profile setting
                notificationScheduler.scheduleDailyNotification(profile.notifyHour)

                val guestName = settingsRepository.guestName.first()

                _uiState.value = HomeUiState.Success(
                    profile = profile,
                    guestName = guestName,
                    currentDayIndex = currentDayIndex,
                    isTodayCompleted = isTodayCompleted,
                    missedDaysCount = catchUpDaysAvailable,
                    catchUpDayIndex = catchUpDayIndex,
                    missedDaysLost = missedDaysLost
                )
            }.onFailure { exception ->
                _uiState.value = HomeUiState.Error(exception.message ?: "Failed to load profile")
            }
        }
    }
}
