package com.tagai

import android.app.Application
import com.tagai.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TagAIApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TagAIApplication)
            modules(appModule)
        }
    }
}