package com.shobdodaily.core.network.model

import com.shobdodaily.core.database.model.ProgressEntity
import com.shobdodaily.core.model.UserProgress
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProgressDto(
    @SerialName("user_id") val userId: String,
    @SerialName("card_id") val cardId: Long,
    @SerialName("seen_at") val seenAt: String? = null,
    @SerialName("completed") val completed: Boolean = false,
    @SerialName("bookmarked") val bookmarked: Boolean = false,
    @SerialName("self_rating") val selfRating: Int? = null
)

fun UserProgressDto.toEntity() = ProgressEntity(
    userId = userId,
    cardId = cardId,
    seenAt = seenAt,
    completed = completed,
    bookmarked = bookmarked,
    selfRating = selfRating
)

fun ProgressEntity.toDomain() = UserProgress(
    userId = userId,
    cardId = cardId,
    seenAt = seenAt,
    completed = completed,
    bookmarked = bookmarked,
    selfRating = selfRating
)

fun UserProgress.toEntity() = ProgressEntity(
    userId = userId,
    cardId = cardId,
    seenAt = seenAt,
    completed = completed,
    bookmarked = bookmarked,
    selfRating = selfRating
)

fun ProgressEntity.toDto() = UserProgressDto(
    userId = userId,
    cardId = cardId,
    seenAt = seenAt,
    completed = completed,
    bookmarked = bookmarked,
    selfRating = selfRating
)
