package com.shobdodaily.core.network.di

import com.shobdodaily.core.model.repository.ProfileRepository
import com.shobdodaily.core.network.repository.SupabaseProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindProfileRepository(
        supabaseProfileRepository: SupabaseProfileRepository
    ): ProfileRepository

    @Binds
    abstract fun bindCardRepository(
        offlineFirstCardRepository: com.shobdodaily.core.network.repository.OfflineFirstCardRepository
    ): com.shobdodaily.core.model.repository.CardRepository

    @Binds
    abstract fun bindProgressRepository(
        offlineFirstProgressRepository: com.shobdodaily.core.network.repository.OfflineFirstProgressRepository
    ): com.shobdodaily.core.model.repository.ProgressRepository

    @Binds
    abstract fun bindQuizRepository(
        offlineFirstQuizRepository: com.shobdodaily.core.network.repository.OfflineFirstQuizRepository
    ): com.shobdodaily.core.model.repository.QuizRepository

    @Binds
    abstract fun bindWordRepository(
        offlineFirstWordRepository: com.shobdodaily.core.network.repository.OfflineFirstWordRepository
    ): com.shobdodaily.core.model.repository.WordRepository

    @Binds
    abstract fun bindAnnouncementRepository(
        supabaseAnnouncementRepository: com.shobdodaily.core.network.repository.SupabaseAnnouncementRepository
    ): com.shobdodaily.core.model.repository.AnnouncementRepository
}
