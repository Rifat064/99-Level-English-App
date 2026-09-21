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
}
