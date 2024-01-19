package com.aman.keys.setting.presentation

import com.aman.keys.auth.domain.model.DeviceData
import com.aman.keys.core.LockAppType

data class SettingState(
    var displayName:String? = "",
    var email:String? = "",
    var profilePhotoUrl: String = "",
    var lockAppSelectedOption:String = LockAppType.NEVER.toString(),
    var loggedInDeviceList:List<DeviceData> = mutableListOf()
)