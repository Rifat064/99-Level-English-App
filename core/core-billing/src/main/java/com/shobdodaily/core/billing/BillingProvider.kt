package com.shobdodaily.core.billing

import android.app.Activity
import kotlinx.coroutines.flow.Flow

interface BillingProvider {
    suspend fun queryProducts(): List<Product>
    suspend fun purchase(activity: Activity, productId: String): PurchaseResult
    suspend fun restore(): PurchaseResult

    fun observeEntitlement(): Flow<EntitlementStatus>
}
