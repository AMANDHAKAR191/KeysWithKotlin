package com.aman.keys.passwords.domain.use_cases

import com.aman.keys.core.util.Response
import com.aman.keys.passwords.domain.model.GeneratedPasswordModelClass
import com.aman.keys.passwords.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow

class SaveRecentGeneratedPassword(
    private val passwordRepository: PasswordRepository
) {
    operator fun invoke(password:String, recentPasswordsList: MutableList<GeneratedPasswordModelClass>): Flow<Response<Pair<String?, Boolean?>>> {
        return passwordRepository.saveGeneratedPassword(password, recentPasswordsList)
    }
}