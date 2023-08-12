package com.aman.keyswithkotlin.auth.domain.use_cases

import com.aman.keyswithkotlin.auth.domain.repository.AuthRepository
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow

class CheckAuthorizationOfDevice(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(deviceId:String): Flow<Response<Pair<String?, Boolean?>>> {
        return authRepository.checkAuthorizationOfDevice(deviceId)
    }
}