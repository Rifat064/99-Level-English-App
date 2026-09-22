package com.shobdodaily.core.model

import java.time.Instant

data class QuizAttempt(
    val id: Long = 0L,
    val userId: String,
    val weekIndex: Int,
    val score: Int?,
    val total: Int?,
    val answers: String?,
    val takenAt: Instant? = null
)
