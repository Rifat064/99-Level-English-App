package com.shobdodaily.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey
    val id: Long,
    val userId: String,
    val weekIndex: Int,
    val score: Int?,
    val total: Int?,
    val answers: String?,
    val takenAt: String?
)
