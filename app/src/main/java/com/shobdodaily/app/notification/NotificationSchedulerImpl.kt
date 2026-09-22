package com.shobdodaily.app.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.shobdodaily.core.model.repository.NotificationScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationScheduler {
    
    private val WORK_NAME = "DailyNotificationWork"

    override fun scheduleDailyNotification(notifyHour: Int) {
        val workManager = WorkManager.getInstance(context)

        val now = LocalDateTime.now()
        val notifyTime = LocalTime.of(notifyHour, 0)
        
        var nextRun = now.with(notifyTime)
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1)
        }
        
        val initialDelay = Duration.between(now, nextRun)
        
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
            .build()
            
        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}
