package com.shobdodaily.feature.history.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shobdodaily.core.model.Card
import com.shobdodaily.core.model.repository.ProfileRepository
import com.shobdodaily.core.model.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewDeckViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReviewUiState>(ReviewUiState.Loading)
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    private val dueCards = mutableListOf<com.shobdodaily.core.model.DailyCardPayload>()
    private var currentIndex = 0

    init {
        loadDueCards()
    }

    private fun loadDueCards() {
        viewModelScope.launch {
            _uiState.value = ReviewUiState.Loading
            try {
                val profile = profileRepository.getProfile().getOrNull()
                val isSubscriber = false // Phase 4.2 handles billing
                
                val result = progressRepository.getDueReviewCards()
                if (result.isSuccess) {
                    val cards = result.getOrNull() ?: emptyList()
                    val limit = if (isSubscriber) Int.MAX_VALUE else 20
                    
                    dueCards.clear()
                    dueCards.addAll(cards.take(limit))
                    currentIndex = 0
                    
                    updateUiState()
                } else {
                    _uiState.value = ReviewUiState.Error("Failed to load review deck")
                }
            } catch (e: Exception) {
                _uiState.value = ReviewUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun submitRating(rating: Int) {
        if (currentIndex < dueCards.size) {
            val currentPayload = dueCards[currentIndex]
            viewModelScope.launch {
                progressRepository.updateSelfRating(currentPayload.card.id, rating)
            }
            currentIndex++
            updateUiState()
        }
    }

    private fun updateUiState() {
        if (currentIndex < dueCards.size) {
            _uiState.value = ReviewUiState.ActiveReview(
                payload = dueCards[currentIndex],
                remainingCount = dueCards.size - currentIndex
            )
        } else {
            _uiState.value = ReviewUiState.Finished
        }
    }
}
