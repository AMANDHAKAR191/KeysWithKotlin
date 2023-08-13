package com.aman.keyswithkotlin.access_verification.domain.repository

import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow

interface  AccessVerificationRepository{
    fun checkAuthorizationOfDevice(deviceId: String): Flow<Response<Pair<String?, Boolean?>>>
}