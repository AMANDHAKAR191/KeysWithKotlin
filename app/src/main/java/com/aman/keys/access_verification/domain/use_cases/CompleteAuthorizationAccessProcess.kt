package com.aman.keys.access_verification.domain.use_cases

import com.aman.keys.access_verification.domain.repository.AccessVerificationRepository
import com.aman.keys.core.util.Response
import kotlinx.coroutines.flow.Flow

class CompleteAuthorizationAccessProcess (
    private val accessVerificationRepository: AccessVerificationRepository
) {
    operator fun invoke(
        primaryDeviceId: String
    ): Flow<Response<Pair<String?, Boolean?>>> {
        return accessVerificationRepository.completeAuthorizationAccessProcess(
            primaryDeviceId = primaryDeviceId
        )
    }
}