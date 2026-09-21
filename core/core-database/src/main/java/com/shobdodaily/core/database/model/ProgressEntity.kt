package com.shobdodaily.core.database.model

import androidx.room.Entity

@Entity(tableName = "user_progress", primaryKeys = ["userId", "cardId"])
data class ProgressEntity(
    val userId: String,
    val cardId: Long,
    val seenAt: String?,
    val completed: Boolean,
    val bookmarked: Boolean,
    val selfRating: Int?
)
