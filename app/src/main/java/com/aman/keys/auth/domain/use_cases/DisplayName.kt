package com.aman.keys.auth.domain.use_cases

import com.aman.keys.auth.domain.repository.AuthRepository
import javax.inject.Inject

class DisplayName @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke():String{
        return authRepository.displayName
    }
}