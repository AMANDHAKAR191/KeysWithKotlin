package com.aman.keyswithkotlin.auth.presentation.profile

import com.aman.keyswithkotlin.auth.domain.model.DeviceData
import com.aman.keyswithkotlin.auth.domain.use_cases.PhotoUrl

data class ProfileState(
    var displayName:String? = "",
    var profilePhotoUrl: String = "",
    var loggedInDeviceList:List<DeviceData> = mutableListOf()
)
