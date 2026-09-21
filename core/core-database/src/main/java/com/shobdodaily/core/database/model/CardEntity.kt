package com.shobdodaily.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey
    val id: Long,
    val dayIndex: Int,
    val season: Int,
    val wordAId: Long?,
    val wordBId: Long?,
    val sentenceEn: String,
    val sentenceBn: String,
    val bubbleText: String?,
    val imagePath: String,
    val imageBlurhash: String?,
    val status: String,
    val publishedAt: String?
)
