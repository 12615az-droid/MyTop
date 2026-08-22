package com.bobon.mygametop

import android.app.Application

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Передаём контекст приложения, чтобы репозитории могли им пользоваться
            androidContext(this@MainApplication)
        }

    }
}