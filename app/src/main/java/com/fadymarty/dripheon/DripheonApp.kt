package com.fadymarty.dripheon

import android.app.Application
import com.fadymarty.dripheon.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DripheonApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@DripheonApp)
            modules(appModule)
        }
    }
}