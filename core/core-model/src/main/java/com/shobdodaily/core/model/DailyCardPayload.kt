package com.shobdodaily.core.model

data class DailyCardPayload(
    val dayIndex: Int,
    val card: Card,
    val wordA: Word,
    val wordB: Word
)
