package com.aman.keys.passwords.domain.use_cases

import com.aman.keys.passwords.domain.model.Password
import com.aman.keys.passwords.domain.repository.PasswordRepository
import com.aman.keys.core.util.Response
import kotlinx.coroutines.flow.Flow

class DeletePassword(
    private val passwordRepository: PasswordRepository
) {
    suspend operator fun invoke(password: Password): Flow<Response<Pair<String?, Boolean?>>> {
        return passwordRepository.deletePassword(password)
    }
}