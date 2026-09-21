package com.shobdodaily.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey
    val id: Long,
    val word: String,
    val pos: String?,
    val bangla: String,
    val englishGloss: String?,
    val examTag: String?,
    val examCategory: String?,
    val difficulty: Int?,
    val frequencyRank: Int?,
    val createdAt: String?
)
