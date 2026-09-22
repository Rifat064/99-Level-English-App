package com.shobdodaily.feature.history.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shobdodaily.core.model.HistoryCardItem
import com.shobdodaily.core.model.repository.CardRepository
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

sealed interface HistoryUiState {
    object Loading : HistoryUiState
    data class Success(val weeks: Map<Int, List<HistoryCardItem>>) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading
            
            val profileResult = profileRepository.getProfile()
            profileResult.onSuccess { profile ->
                val zoneId = ZoneId.of(profile.timezone)
                val enrolledDate = profile.enrolledAt.atZone(zoneId).toLocalDate()
                val todayDate = Instant.now().atZone(zoneId).toLocalDate()
                val daysDifference = ChronoUnit.DAYS.between(enrolledDate, todayDate).toInt()
                val currentDayIndex = maxOf(1, daysDifference + 1)

                // Sync history from remote
                cardRepository.syncHistory(currentDayIndex)

                // Observe local history
                // isSubscriber = false for now (Phase 4.2 handles billing)
                cardRepository.observeHistory(currentDayIndex, currentDayIndex, false)
                    .collect { cards ->
                        // Group by week. Week 1 is days 1..7, Week 2 is 8..14, etc.
                        val weeks = cards.groupBy { (it.dayIndex - 1) / 7 + 1 }
                        _uiState.value = HistoryUiState.Success(weeks)
                    }
            }.onFailure { exception ->
                _uiState.value = HistoryUiState.Error(exception.message ?: "Failed to load history")
            }
        }
    }
}
