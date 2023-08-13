package com.aman.keyswithkotlin.access_verification.domain.use_cases

import com.aman.keyswithkotlin.access_verification.domain.repository.AccessVerificationRepository
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow

class CheckAuthorizationOfDevice(
    private val accessVerificationRepository: AccessVerificationRepository
) {
    suspend operator fun invoke(deviceId:String): Flow<Response<Pair<String?, Boolean?>>> {
        println("check3")
        return accessVerificationRepository.checkAuthorizationOfDevice(deviceId)
    }
}