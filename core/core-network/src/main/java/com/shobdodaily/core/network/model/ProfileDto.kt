package com.shobdodaily.core.network.model

import com.shobdodaily.core.model.Profile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ProfileDto(
    @SerialName("id") val id: String,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null,
    @SerialName("enrolled_at") val enrolledAt: String,
    @SerialName("words_per_day") val wordsPerDay: Int,
    @SerialName("timezone") val timezone: String,
    @SerialName("notify_hour") val notifyHour: Int,
    @SerialName("streak_count") val streakCount: Int,
    @SerialName("longest_streak") val longestStreak: Int,
    @SerialName("last_completed_day") val lastCompletedDay: Int
)

fun ProfileDto.toDomain() = Profile(
    id = id,
    displayName = displayName,
    photoUrl = photoUrl,
    enrolledAt = Instant.parse(enrolledAt),
    wordsPerDay = wordsPerDay,
    timezone = timezone,
    notifyHour = notifyHour,
    streakCount = streakCount,
    longestStreak = longestStreak,
    lastCompletedDay = lastCompletedDay
)

@Serializable
data class ProfileStreakUpdateDto(
    @SerialName("streak_count") val streakCount: Int,
    @SerialName("longest_streak") val longestStreak: Int,
    @SerialName("last_completed_day") val lastCompletedDay: Int
)
