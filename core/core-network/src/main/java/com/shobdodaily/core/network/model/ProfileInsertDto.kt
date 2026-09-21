package com.shobdodaily.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileInsertDto(
    @SerialName("id") val id: String,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("timezone") val timezone: String = "Asia/Dhaka",
    @SerialName("words_per_day") val wordsPerDay: Int = 2,
    @SerialName("notify_hour") val notifyHour: Int = 8
)
