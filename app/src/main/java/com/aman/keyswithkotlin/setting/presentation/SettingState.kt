package com.aman.keyswithkotlin.setting.presentation

import com.aman.keyswithkotlin.auth.domain.model.DeviceData
data class SettingState(
    var displayName:String? = "",
    var email:String? = "",
    var profilePhotoUrl: String = "",
    var lockAppSelectedOption:String = LockAppType.NEVER.toString(),
    var loggedInDeviceList:List<DeviceData> = mutableListOf()
)