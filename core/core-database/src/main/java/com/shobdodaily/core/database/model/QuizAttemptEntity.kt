package com.shobdodaily.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "quiz_attempts",
    indices = [
        androidx.room.Index(value = ["userId", "weekIndex"], unique = true)
    ]
)
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
