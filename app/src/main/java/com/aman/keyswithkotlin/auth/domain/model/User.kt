package com.aman.keyswithkotlin.auth.domain.model

import com.aman.keyswithkotlin.core.AES

data class User(
    var displayName: String? = "",
    var email: String? = "",
    var photoUrl: String? = "",
    var aesKey: String? = "",
    var aesIV: String? = "",
    var privateUID: String? = "",
    var publicUID: String? = "",
    var createdAt: String? = "",
    var userDevicesList: DeviceData? = null,
)

data class DeviceData(
    val deviceId:String? = "",
    val isAuthorize:Boolean = false,
    val deviceType: String? = DeviceType.PHONE.toString(),
    val appVersion:String? = "",
    val lastLoginTimeStamp:String? = "",
    val ipAddress:String? = ""
)

enum class DeviceType{
    PHONE, TABLET
}


