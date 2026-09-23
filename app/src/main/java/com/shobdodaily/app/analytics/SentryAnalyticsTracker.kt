package com.shobdodaily.app.analytics

import com.shobdodaily.core.model.AnalyticsTracker
import io.sentry.Sentry
import io.sentry.protocol.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SentryAnalyticsTracker @Inject constructor() : AnalyticsTracker {
    override fun trackScreenView(screenName: String) {
        Sentry.addBreadcrumb("Screen view: $screenName")
    }

    override fun trackEvent(eventName: String, properties: Map<String, String>?) {
        Sentry.addBreadcrumb("Event: $eventName")
        Sentry.captureMessage("Analytics Event: $eventName")
    }

    override fun setUserId(userId: String?) {
        if (userId != null) {
            val user = User().apply {
                id = userId
            }
            Sentry.setUser(user)
        } else {
            Sentry.setUser(null)
        }
    }
}
