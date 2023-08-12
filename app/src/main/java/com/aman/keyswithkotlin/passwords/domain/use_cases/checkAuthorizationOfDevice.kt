package com.aman.keyswithkotlin.passwords.domain.use_cases

import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow

class CheckAuthorizationOfDevice(
    private val passwordRepository: PasswordRepository
) {
    suspend operator fun invoke(deviceId:String): Flow<Response<Pair<String?, Boolean?>>> {
        return passwordRepository.checkAuthorizationOfDevice(deviceId)
    }
}