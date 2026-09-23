package com.shobdodaily.app.di

import com.shobdodaily.app.analytics.SentryAnalyticsTracker
import com.shobdodaily.core.model.AnalyticsTracker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {
    @Binds
    abstract fun bindAnalyticsTracker(
        sentryAnalyticsTracker: SentryAnalyticsTracker
    ): AnalyticsTracker
}
