package com.example.carshare

import android.app.Application
import com.example.carshare.BuildConfig
import com.stripe.android.PaymentConfiguration

class FirebaseMobilePaymentsApp : Application(){
    override fun onCreate() {
        super.onCreate()
        PaymentConfiguration.init(applicationContext, BuildConfig.PublishableKey)
    }
}