package com.shobdodaily.feature.quiz.domain

data class Quiz(
    val weekIndex: Int,
    val questions: List<Question>
)
