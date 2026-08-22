package com.bobon.mygametop

import android.app.Application
import com.bobon.mygametop.data.di.dataModule
import com.bobon.mygametop.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(dataModule, appModule)
        }
    }
}
