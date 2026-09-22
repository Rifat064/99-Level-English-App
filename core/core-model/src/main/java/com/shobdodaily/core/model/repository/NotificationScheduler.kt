package com.shobdodaily.core.model.repository

interface NotificationScheduler {
    fun scheduleDailyNotification(notifyHour: Int)
}
