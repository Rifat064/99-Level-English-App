package com.shobdodaily.feature.history.review

import com.shobdodaily.core.model.Card

sealed interface ReviewUiState {
    object Loading : ReviewUiState
    data class ActiveReview(
        val payload: com.shobdodaily.core.model.DailyCardPayload,
        val remainingCount: Int
    ) : ReviewUiState
    object Finished : ReviewUiState
    data class Error(val message: String) : ReviewUiState
}
