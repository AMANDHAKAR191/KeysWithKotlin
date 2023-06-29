package com.aman.keyswithkotlin.passwords.domain.use_cases

import com.aman.keyswithkotlin.passwords.domain.model.RealtimeModelResponse
import com.aman.keyswithkotlin.passwords.domain.repository.PasswordRepository
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow

class GetPasswords (
    private val passwordRepository: PasswordRepository
) {
    suspend operator fun invoke():Flow<Response<List<RealtimeModelResponse>>>{
        return passwordRepository.getPasswords()
    }
}