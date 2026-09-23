package com.shobdodaily.core.model

import java.time.Instant

data class Announcement(
    val id: Long,
    val type: String, // 'EVENT' or 'EDITORS_CHOICE'
    val title: String,
    val message: String,
    val targetWordId: Long?,
    val actionUrl: String?,
    val isActive: Boolean,
    val createdAt: Instant
)
