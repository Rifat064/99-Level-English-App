package com.shobdodaily.core.model

data class Card(
    val id: Long,
    val dayIndex: Int,
    val season: Int,
    val wordAId: Long,
    val wordBId: Long,
    val sentenceEn: String,
    val sentenceBn: String,
    val bubbleText: String?,
    val imagePath: String,
    val imageBlurhash: String?,
    val status: String
)
