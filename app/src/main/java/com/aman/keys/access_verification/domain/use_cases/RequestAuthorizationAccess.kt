package com.aman.keys.access_verification.domain.use_cases

import com.aman.keys.access_verification.domain.repository.AccessVerificationRepository
import com.aman.keys.core.util.Response
import kotlinx.coroutines.flow.Flow

class RequestAuthorizationAccess(
    private val accessVerificationRepository: AccessVerificationRepository
) {
    operator fun invoke(
        primaryDeviceId: String,
        authorizationCode:Int,
        requestingDeviceId: String
    ): Flow<Response<Pair<String?, Boolean?>>> {
        return accessVerificationRepository.requestAuthorizationAccess(
            primaryDeviceId = primaryDeviceId,
            authorizationCode = authorizationCode,
            requestingDeviceId = requestingDeviceId
        )
    }
}