package com.shobdodaily.core.billing

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeBillingProvider @Inject constructor() : BillingProvider {

    private val entitlementStatus = MutableStateFlow(EntitlementStatus.NONE)

    private val fakeProducts = listOf(
        Product("monthly_sub", "Monthly Subscription", "৳ 99", "Access all features for 1 month"),
        Product("six_month_sub", "6-Month Subscription", "৳ 499", "Access all features for 6 months"),
        Product("lifetime_sub", "Lifetime Access", "৳ 1499", "Unlock everything forever")
    )

    override suspend fun queryProducts(): List<Product> {
        delay(500) // Simulate network delay
        return fakeProducts
    }

    override suspend fun purchase(activity: android.app.Activity, productId: String): PurchaseResult {
        delay(800) // Simulate purchase flow delay
        return if (fakeProducts.any { it.id == productId }) {
            entitlementStatus.value = EntitlementStatus.ACTIVE
            PurchaseResult.Success(UUID.randomUUID().toString())
        } else {
            PurchaseResult.Error("Product not found")
        }
    }

    override suspend fun restore(): PurchaseResult {
        delay(800) // Simulate restore delay
        // In fake, let's assume no prior purchase for a fresh state, or we could toggle it.
        // Returning success with a mock token to simulate a successful restore if we wanted to test the UI.
        entitlementStatus.value = EntitlementStatus.ACTIVE
        return PurchaseResult.Success(UUID.randomUUID().toString())
    }

    override fun observeEntitlement(): Flow<EntitlementStatus> {
        return entitlementStatus
    }
}
