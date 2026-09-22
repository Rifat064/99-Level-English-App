package com.shobdodaily.core.model

data class HistoryCardItem(
    val cardId: Long,
    val dayIndex: Int,
    val season: Int,
    val imageBlurhash: String?,
    val isCompleted: Boolean,
    val isLocked: Boolean
)
