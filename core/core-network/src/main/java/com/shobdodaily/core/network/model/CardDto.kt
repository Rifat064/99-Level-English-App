package com.shobdodaily.core.network.model

import com.shobdodaily.core.model.Card
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CardDto(
    @SerialName("id") val id: Long,
    @SerialName("day_index") val dayIndex: Int,
    @SerialName("season") val season: Int,
    @SerialName("word_a_id") val wordAId: Long,
    @SerialName("word_b_id") val wordBId: Long,
    @SerialName("sentence_en") val sentenceEn: String,
    @SerialName("sentence_bn") val sentenceBn: String,
    @SerialName("bubble_text") val bubbleText: String? = null,
    @SerialName("image_path") val imagePath: String,
    @SerialName("image_blurhash") val imageBlurhash: String? = null,
    @SerialName("status") val status: String
)

fun CardDto.toDomain() = Card(
    id = id,
    dayIndex = dayIndex,
    season = season,
    wordAId = wordAId,
    wordBId = wordBId,
    sentenceEn = sentenceEn,
    sentenceBn = sentenceBn,
    bubbleText = bubbleText,
    imagePath = imagePath,
    imageBlurhash = imageBlurhash,
    status = status
)

fun CardDto.toEntity() = com.shobdodaily.core.database.model.CardEntity(
    id = id,
    dayIndex = dayIndex,
    season = season,
    wordAId = wordAId,
    wordBId = wordBId,
    sentenceEn = sentenceEn,
    sentenceBn = sentenceBn,
    bubbleText = bubbleText,
    imagePath = imagePath,
    imageBlurhash = imageBlurhash,
    status = status,
    publishedAt = null
)
