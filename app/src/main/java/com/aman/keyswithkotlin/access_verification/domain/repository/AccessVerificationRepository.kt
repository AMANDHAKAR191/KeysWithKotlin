package com.aman.keyswithkotlin.access_verification.domain.repository

import com.aman.keyswithkotlin.auth.domain.model.DeviceData
import com.aman.keyswithkotlin.auth.domain.model.RequestAuthorizationAccess
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow

interface  AccessVerificationRepository{
    fun checkAuthorizationOfDevice(deviceId: String): Flow<Response<Pair<List<DeviceData>?, Boolean?>>>

    fun giveAuthorizationAccessOfSecondaryDevice(deviceId: String): Flow<Response<Pair<String?, Boolean?>>>

    fun removeAuthorizationAccessOfSecondaryDevice(deviceId: String): Flow<Response<Pair<String?, Boolean?>>>
    fun getAccessRequesterClient(deviceId: String): Flow<Response<Pair<RequestAuthorizationAccess?, Boolean?>>>

    fun requestAuthorizationAccess(primaryDeviceId:String,authorizationCode:Int, requestingDeviceId:String): Flow<Response<Pair<String?, Boolean?>>>

    fun completeAuthorizationAccessProcess(primaryDeviceId:String): Flow<Response<Pair<String?, Boolean?>>>
}