package com.aman.keys.auth.presentation.profile

data class ProfileState(
    var displayName:String? = "",
    var email:String? = "",
    var profilePhotoUrl: String = "",
//    var loggedInDeviceList:List<DeviceData> = mutableListOf()
)
