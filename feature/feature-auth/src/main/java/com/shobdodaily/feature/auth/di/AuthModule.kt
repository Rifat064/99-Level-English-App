package com.shobdodaily.feature.auth.di

import com.shobdodaily.feature.auth.data.SupabaseAuthRepository
import com.shobdodaily.feature.auth.domain.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    abstract fun bindAuthRepository(
        impl: SupabaseAuthRepository
    ): AuthRepository
}
