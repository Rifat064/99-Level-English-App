package com.shobdodaily.feature.card.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shobdodaily.core.model.DailyCardPayload
import com.shobdodaily.core.model.UserProgress
import com.shobdodaily.core.model.repository.CardRepository
import com.shobdodaily.core.model.repository.ProgressRepository
import com.shobdodaily.core.model.repository.ProfileRepository
import com.shobdodaily.core.datastore.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DailyCardUiState(
    val payload: DailyCardPayload? = null,
    val progress: UserProgress? = null,
    val isSubscriber: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val ttsAccent: String = "en-US"
)

@HiltViewModel
class DailyCardViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val progressRepository: ProgressRepository,
    private val profileRepository: ProfileRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyCardUiState())
    val uiState: StateFlow<DailyCardUiState> = _uiState.asStateFlow()

    private var currentDayIndex: Int = -1
    private var currentCardId: Long = -1L

    private var progressJob: kotlinx.coroutines.Job? = null

    init {
        viewModelScope.launch {
            settingsRepository.ttsAccent.collect { accent ->
                _uiState.update { it.copy(ttsAccent = accent) }
            }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun loadCard(dayIndex: Int) {
        if (currentDayIndex == dayIndex) return
        currentDayIndex = dayIndex
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            cardRepository.observeCard(dayIndex).collect { cardResult ->
                cardResult.onSuccess { payload ->
                    currentCardId = payload.card.id
                    val isSub = false // Phase 4.2 handles billing
                    _uiState.update { it.copy(payload = payload, isSubscriber = isSub, isLoading = false) }
                    
                    progressJob?.cancel()
                    progressJob = viewModelScope.launch {
                        progressRepository.observeProgress(payload.card.id).collect { progress ->
                            _uiState.update { it.copy(progress = progress) }
                        }
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            }
        }
    }

    fun markAsLearned() {
        if (currentCardId == -1L || currentDayIndex == -1) return
        viewModelScope.launch {
            // First mark as learned in progress repo
            val result = progressRepository.markAsLearned(currentCardId)
            result.onSuccess {
                // Then record completion for streak in profile repo
                profileRepository.recordCompletion(currentDayIndex)
            }
        }
    }

    fun toggleBookmark() {
        if (currentCardId == -1L) return
        viewModelScope.launch {
            progressRepository.toggleBookmark(currentCardId)
        }
    }
}
