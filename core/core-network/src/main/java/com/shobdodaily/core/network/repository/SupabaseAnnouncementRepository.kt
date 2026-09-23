package com.shobdodaily.core.network.repository

import com.shobdodaily.core.model.Announcement
import com.shobdodaily.core.model.repository.AnnouncementRepository
import com.shobdodaily.core.network.model.AnnouncementDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import java.time.Instant
import javax.inject.Inject

class SupabaseAnnouncementRepository @Inject constructor(
    private val supabase: SupabaseClient
) : AnnouncementRepository {

    override suspend fun getActiveAnnouncements(): List<Announcement> {
        return try {
            val dtos = supabase.postgrest["global_announcements"]
                .select {
                    filter {
                        eq("is_active", true)
                    }
                }
                .decodeList<AnnouncementDto>()

            dtos.map { dto ->
                Announcement(
                    id = dto.id,
                    type = dto.type,
                    title = dto.title,
                    message = dto.message,
                    targetWordId = dto.targetWordId,
                    actionUrl = dto.actionUrl,
                    isActive = dto.isActive,
                    createdAt = Instant.parse(dto.createdAt)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
