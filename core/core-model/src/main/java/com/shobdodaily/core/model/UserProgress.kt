package com.shobdodaily.core.model

data class UserProgress(
    val userId: String,
    val cardId: Long,
    val seenAt: String?,
    val completed: Boolean,
    val bookmarked: Boolean,
    val selfRating: Int?
)
