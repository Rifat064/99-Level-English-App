package com.shobdodaily.core.model

interface AnalyticsTracker {
    fun trackScreenView(screenName: String)
    fun trackEvent(eventName: String, properties: Map<String, String>? = null)
    fun setUserId(userId: String?)
}
