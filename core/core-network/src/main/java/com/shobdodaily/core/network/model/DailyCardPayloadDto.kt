package com.shobdodaily.core.network.model

import com.shobdodaily.core.model.DailyCardPayload
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyCardPayloadDto(
    @SerialName("day_index") val dayIndex: Int,
    @SerialName("card") val card: CardDto,
    @SerialName("word_a") val wordA: WordDto,
    @SerialName("word_b") val wordB: WordDto
)

fun DailyCardPayloadDto.toDomain() = DailyCardPayload(
    dayIndex = dayIndex,
    card = card.toDomain(),
    wordA = wordA.toDomain(),
    wordB = wordB.toDomain()
)
