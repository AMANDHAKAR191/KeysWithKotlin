package com.aman.keyswithkotlin.access_verification.domain.use_cases

import com.aman.keyswithkotlin.access_verification.domain.repository.AccessVerificationRepository
import com.aman.keyswithkotlin.auth.domain.model.RequestAuthorizationAccess
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow

class GetAccessRequesterClient(
    private val accessVerificationRepository: AccessVerificationRepository
) {
    operator fun invoke(deviceId:String): Flow<Response<Pair<RequestAuthorizationAccess?, Boolean?>>> {
        return accessVerificationRepository.getAccessRequesterClient(deviceId)
    }
}