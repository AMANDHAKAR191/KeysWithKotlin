package com.aman.keys.access_verification.domain.use_cases

import com.aman.keys.access_verification.domain.repository.AccessVerificationRepository
import com.aman.keys.core.util.Response
import kotlinx.coroutines.flow.Flow

class RemoveAuthorizationAccessOfSecondaryDevice (
    private val accessVerificationRepository: AccessVerificationRepository
) {
    operator fun invoke(deviceId:String): Flow<Response<Pair<String?, Boolean?>>> {
        return accessVerificationRepository.removeAuthorizationAccessOfSecondaryDevice(deviceId)
    }
}