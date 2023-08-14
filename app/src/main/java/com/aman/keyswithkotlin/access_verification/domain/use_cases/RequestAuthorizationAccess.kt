package com.aman.keyswithkotlin.access_verification.domain.use_cases

import com.aman.keyswithkotlin.access_verification.domain.repository.AccessVerificationRepository
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow

class RequestAuthorizationAccess(
    private val accessVerificationRepository: AccessVerificationRepository
) {
    operator fun invoke(
        primaryDeviceId: String,
        requestingDeviceId: String
    ): Flow<Response<Pair<String?, Boolean?>>> {
        return accessVerificationRepository.requestAuthorizationAccess(
            primaryDeviceId = primaryDeviceId,
            requestingDeviceId = requestingDeviceId
        )
    }
}