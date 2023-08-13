package com.aman.keyswithkotlin.core

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.aman.keyswithkotlin.core.util.TimeStampUtil

class DeviceInfo(private val context: Context) {

    fun getDeviceId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun getDeviceName(): String {
        //to get device name
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }

    fun getDeviceBuildNumber(): String {
        //to get build number
        return Build.ID
    }

    fun getOSAndVersion(): String {
        return "OS: Android, Version: ${Build.VERSION.RELEASE}"
    }

    fun getLastLoginTimeStamp(): String {
        val timeStampUtil = TimeStampUtil()
        return timeStampUtil.generateTimestamp()  // Placeholder value.
    }

    fun getAppVersion(): String {
        return context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }

    fun getIpAddress(): String {

        // Your code to retrieve the IP address goes here.
        return "IP Address" // Placeholder value.
    }

    suspend fun getPublicIPAddress(): String {
        return "1234"
    }
}