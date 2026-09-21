package com.shobdodaily.core.model

data class Word(
    val id: Long,
    val word: String,
    val pos: String?,
    val bangla: String,
    val englishGloss: String?,
    val examTag: String?,
    val examCategory: String?,
    val difficulty: Int?,
    val frequencyRank: Int?
)
