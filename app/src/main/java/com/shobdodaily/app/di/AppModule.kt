package com.shobdodaily.app.di

import com.shobdodaily.app.notification.NotificationSchedulerImpl
import com.shobdodaily.core.model.repository.NotificationScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindNotificationScheduler(
        notificationSchedulerImpl: NotificationSchedulerImpl
    ): NotificationScheduler
}
