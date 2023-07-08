package com.aman.keyswithkotlin.passwords.domain.use_cases

import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.repository.PasswordRepository
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow

class DeletePassword(
    private val passwordRepository: PasswordRepository
) {
    suspend operator fun invoke(password: Password): Flow<Response<Pair<String?, Boolean?>>> {
        return passwordRepository.deletePassword(password)
    }
}