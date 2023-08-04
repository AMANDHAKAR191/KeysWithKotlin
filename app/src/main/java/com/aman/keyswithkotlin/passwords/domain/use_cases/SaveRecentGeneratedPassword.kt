package com.aman.keyswithkotlin.passwords.domain.use_cases

import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.passwords.domain.model.GeneratedPasswordModelClass
import com.aman.keyswithkotlin.passwords.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow

class SaveRecentGeneratedPassword(
    private val passwordRepository: PasswordRepository
) {
    operator fun invoke(password:String, recentPasswordsList: MutableList<GeneratedPasswordModelClass>): Flow<Response<Pair<String?, Boolean?>>> {
        return passwordRepository.saveGeneratedPassword(password, recentPasswordsList)
    }
}