package com.shobdodaily.core.network.model

import com.shobdodaily.core.model.Word
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WordDto(
    @SerialName("id") val id: Long,
    @SerialName("word") val word: String,
    @SerialName("pos") val pos: String? = null,
    @SerialName("bangla") val bangla: String,
    @SerialName("english_gloss") val englishGloss: String? = null,
    @SerialName("exam_tag") val examTag: String? = null,
    @SerialName("exam_category") val examCategory: String? = null,
    @SerialName("difficulty") val difficulty: Int? = null,
    @SerialName("frequency_rank") val frequencyRank: Int? = null
)

fun WordDto.toDomain() = Word(
    id = id,
    word = word,
    pos = pos,
    bangla = bangla,
    englishGloss = englishGloss,
    examTag = examTag,
    examCategory = examCategory,
    difficulty = difficulty,
    frequencyRank = frequencyRank
)

fun WordDto.toEntity() = com.shobdodaily.core.database.model.WordEntity(
    id = id,
    word = word,
    pos = pos,
    bangla = bangla,
    englishGloss = englishGloss,
    examTag = examTag,
    examCategory = examCategory,
    difficulty = difficulty,
    frequencyRank = frequencyRank,
    createdAt = null
)
