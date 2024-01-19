package com.aman.keys.access_verification.domain.use_cases

import com.aman.keys.access_verification.domain.repository.AccessVerificationRepository
import com.aman.keys.auth.domain.model.RequestAuthorizationAccess
import com.aman.keys.core.util.Response
import kotlinx.coroutines.flow.Flow

class GetAccessRequesterClient(
    private val accessVerificationRepository: AccessVerificationRepository
) {
    operator fun invoke(deviceId:String): Flow<Response<Pair<RequestAuthorizationAccess?, Boolean?>>> {
        return accessVerificationRepository.getAccessRequesterClient(deviceId)
    }
}