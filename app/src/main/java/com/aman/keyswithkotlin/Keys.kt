package com.aman.keyswithkotlin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Keys : Application() {
    companion object {
        lateinit var instance: Keys
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}