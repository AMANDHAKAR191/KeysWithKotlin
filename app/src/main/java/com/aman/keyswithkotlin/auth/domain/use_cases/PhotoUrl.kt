package com.aman.keyswithkotlin.auth.domain.use_cases

import com.aman.keyswithkotlin.auth.domain.repository.AuthRepository
import javax.inject.Inject

class PhotoUrl @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke():String{
        return authRepository.photoUrl
    }
}