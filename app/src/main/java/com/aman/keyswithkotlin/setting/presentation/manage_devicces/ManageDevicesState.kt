package com.aman.keyswithkotlin.setting.presentation.manage_devicces

import com.aman.keyswithkotlin.auth.domain.model.DeviceData

data class ManageDevicesState(
    var loggedInDeviceList:List<DeviceData> = mutableListOf()
)