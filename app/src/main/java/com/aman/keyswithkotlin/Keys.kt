package com.aman.keyswithkotlin

import android.app.Application
import com.aman.keyswithkotlin.autofill_service.KeysAutofillService
import com.aman.keyswithkotlin.di.password.PasswordModule
import com.google.android.datatransport.runtime.dagger.Component
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