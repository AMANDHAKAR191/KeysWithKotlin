package com.aman.keyswithkotlin.passwords.domain.use_cases

import com.aman.keyswithkotlin.passwords.domain.model.InvalidPasswordException
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.repository.PasswordRepository
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.Throws

class AddPassword(
    private val passwordRepository: PasswordRepository
) {
    @Throws(InvalidPasswordException::class)
    suspend operator fun invoke(password: Password): Flow<Response<Pair<String?, Boolean?>>> {
        if (password.userName.isBlank()){
            throw InvalidPasswordException("The username can't be empty.")
        }
        if (password.password.isBlank()){
            throw InvalidPasswordException("The password can't be empty.")
        }
        if (password.websiteName.isBlank()){
            throw InvalidPasswordException("The website name can't be empty.")
        }
        return passwordRepository.insertPassword(password)
    }
}