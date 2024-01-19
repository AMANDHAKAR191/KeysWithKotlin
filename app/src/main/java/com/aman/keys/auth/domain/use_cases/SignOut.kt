package com.aman.keys.auth.domain.use_cases

import com.aman.keys.auth.domain.repository.AuthRepository
import com.aman.keys.auth.domain.repository.SignOutResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignOut @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<SignOutResponse> {
        return authRepository.signOut()
    }
}