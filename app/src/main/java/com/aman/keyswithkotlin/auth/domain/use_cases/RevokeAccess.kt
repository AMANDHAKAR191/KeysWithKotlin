package com.aman.keyswithkotlin.auth.domain.use_cases

import com.aman.keyswithkotlin.auth.domain.repository.AuthRepository
import com.aman.keyswithkotlin.auth.domain.repository.RevokeAccessResponse
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RevokeAccess @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<RevokeAccessResponse> {
        return authRepository.revokeAccess()
    }
}