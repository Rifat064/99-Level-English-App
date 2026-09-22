package com.shobdodaily.core.billing

data class Product(
    val id: String,
    val title: String,
    val price: String,
    val description: String
)

sealed class PurchaseResult {
    data class Success(val purchaseToken: String) : PurchaseResult()
    data class Error(val message: String) : PurchaseResult()
    data object UserCanceled : PurchaseResult()
}

enum class EntitlementStatus {
    ACTIVE,
    GRACE,
    EXPIRED,
    REFUNDED,
    NONE
}
