package com.aman.keys.setting.presentation.manage_devicces

import com.aman.keys.auth.domain.model.DeviceData

data class ManageDevicesState(
    var loggedInDeviceList:List<DeviceData> = mutableListOf()
)