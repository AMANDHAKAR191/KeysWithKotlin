package com.aman.keyswithkotlin.passwords.domain.use_cases

import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.passwords.domain.model.GeneratedPasswordModelClass
import com.aman.keyswithkotlin.passwords.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow

class GetRecentGeneratedPasswords(
    private val passwordRepository: PasswordRepository
) {
    operator fun invoke(): Flow<Response<Pair<MutableList<GeneratedPasswordModelClass>?, Boolean?>>> {
        return passwordRepository.getRecentGeneratedPasswords()
    }
}