package com.shobdodaily.core.model.repository

import com.shobdodaily.core.model.Announcement

interface AnnouncementRepository {
    suspend fun getActiveAnnouncements(): List<Announcement>
}
