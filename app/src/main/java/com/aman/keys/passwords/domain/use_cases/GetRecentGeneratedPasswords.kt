package com.aman.keys.passwords.domain.use_cases

import com.aman.keys.core.util.Response
import com.aman.keys.passwords.domain.model.GeneratedPasswordModelClass
import com.aman.keys.passwords.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow

class GetRecentGeneratedPasswords(
    private val passwordRepository: PasswordRepository
) {
    operator fun invoke(): Flow<Response<Pair<MutableList<GeneratedPasswordModelClass>?, Boolean?>>> {
        return passwordRepository.getRecentGeneratedPasswords()
    }
}