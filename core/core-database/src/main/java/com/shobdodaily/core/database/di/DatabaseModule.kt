package com.shobdodaily.core.database.di

import android.content.Context
import androidx.room.Room
import com.shobdodaily.core.database.ShobdoDailyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesShobdoDailyDatabase(
        @ApplicationContext context: Context,
    ): ShobdoDailyDatabase = Room.databaseBuilder(
        context,
        ShobdoDailyDatabase::class.java,
        "shobdodaily-database"
    ).build()

    @Provides
    fun providesCardDao(
        database: ShobdoDailyDatabase,
    ) = database.cardDao()

    @Provides
    fun providesWordDao(
        database: ShobdoDailyDatabase,
    ) = database.wordDao()

    @Provides
    fun providesProgressDao(
        database: ShobdoDailyDatabase,
    ) = database.progressDao()

    @Provides
    fun providesQuizAttemptDao(
        database: ShobdoDailyDatabase,
    ) = database.quizAttemptDao()
}
