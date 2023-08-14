package com.aman.keyswithkotlin.auth.domain.model

import com.aman.keyswithkotlin.core.AES
import com.aman.keyswithkotlin.core.Authentication
import com.aman.keyswithkotlin.core.Authorization
import com.aman.keyswithkotlin.core.DeviceType

data class User(
    var displayName: String? = "",
    var email: String? = "",
    var photoUrl: String? = "",
    var aesKey: String? = "",
    var aesIV: String? = "",
    var privateUID: String? = "",
    var publicUID: String? = "",
    var createdAt: String? = "",
    var userDevicesList: Map<String, DeviceData>? = null,
)

data class DeviceData(
    val deviceId:String? = "",
    val deviceName:String? = "",
    val deviceBuildNumber:String? = "",
    val authorization:String = Authorization.NotAuthorized.toString(),
    val authentication:String? = Authentication.NotAuthenticated.toString(),
    val deviceType: String? = DeviceType.Secondary.toString(),
    val appVersion:String? = "",
    val lastLoginTimeStamp:String? = "",
    val ipAddress:String? = "",
    val requestAuthorizationAccess: RequestAuthorizationAccess = RequestAuthorizationAccess()
)

data class RequestAuthorizationAccess(
    val requesterID:String? = "",
    val requestingAccess:Boolean = false
)



