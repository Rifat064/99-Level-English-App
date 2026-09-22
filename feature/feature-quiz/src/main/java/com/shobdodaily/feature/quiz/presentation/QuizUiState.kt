package com.shobdodaily.feature.quiz.presentation

import com.shobdodaily.feature.quiz.domain.Question

sealed interface QuizUiState {
    object Loading : QuizUiState
    
    data class ActiveQuestion(
        val question: Question,
        val currentQuestionIndex: Int,
        val totalQuestions: Int,
        val selectedOptionIndex: Int?
    ) : QuizUiState
    
    data class Finished(
        val totalQuestions: Int,
        val score: Int,
        val missedWords: List<com.shobdodaily.core.model.Word>
    ) : QuizUiState
    
    data class Error(val message: String) : QuizUiState
}
