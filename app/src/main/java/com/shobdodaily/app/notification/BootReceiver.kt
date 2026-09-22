package com.shobdodaily.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.shobdodaily.core.model.repository.ProfileRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var profileRepository: ProfileRepository

    @Inject
    lateinit var notificationScheduler: com.shobdodaily.core.model.repository.NotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val profileResult = profileRepository.getProfile()
                profileResult.onSuccess { profile ->
                    notificationScheduler.scheduleDailyNotification(profile.notifyHour)
                }
            }
        }
    }
}
