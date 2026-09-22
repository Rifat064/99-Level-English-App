package com.shobdodaily.core.billing.di

import com.shobdodaily.core.billing.BillingProvider
import com.shobdodaily.core.billing.FakeBillingProvider
import com.shobdodaily.core.billing.PlayBillingProvider
import com.shobdodaily.core.billing.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

    @Provides
    @Singleton
    fun provideBillingProvider(
        fakeBillingProvider: FakeBillingProvider,
        playBillingProvider: PlayBillingProvider
    ): BillingProvider {
        return if (BuildConfig.DEBUG) {
            fakeBillingProvider
        } else {
            playBillingProvider
        }
    }
}
