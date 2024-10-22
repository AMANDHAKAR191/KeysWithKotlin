package com.aman.keys.passwords.domain.use_cases

import com.aman.keys.core.AES
import com.aman.keys.core.util.Response
import com.aman.keys.di.AESKeySpecs
import com.aman.keys.passwords.domain.model.InvalidPasswordException
import com.aman.keys.passwords.domain.model.Password
import com.aman.keys.passwords.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow

class AddPassword(
    private val passwordRepository: PasswordRepository,
    private val aesCloudKeySpecs: AESKeySpecs,
    private val aesLocalKeySpecs: AESKeySpecs
) {
    @Throws(InvalidPasswordException::class)
    operator fun invoke(password: Password): Flow<Response<Pair<String?, Boolean?>>> {
        println("AddPassword: check3")
        println("aesCloudKeySpecs: ${aesCloudKeySpecs.aesKey} || ${aesCloudKeySpecs.aesIV}")
        println("aesLocalKeySpecs: ${aesLocalKeySpecs.aesKey} || ${aesLocalKeySpecs.aesIV}")
        if (password.userName.isBlank()) {
            throw InvalidPasswordException("The username can't be empty.")
        }
        if (password.password.isBlank()) {
            throw InvalidPasswordException("The password can't be empty.")
        }
        if (password.websiteName.isBlank()) {
            throw InvalidPasswordException("The website name can't be empty.")
        }
        println("AddPassword: check4")
        val aes = AES.getInstance(aesCloudKeySpecs.aesKey, aesCloudKeySpecs.aesIV)
            ?: throw IllegalStateException("Failed to initialize AES instance.")

        val encryptedPassword = encryptPassword(password, aes)
        return passwordRepository.insertPassword(encryptedPassword)
    }

    private fun encryptPassword(password: Password, aes: AES): Password {
        val encryptedPassword = password.copy()
        encryptedPassword.userName = aes.singleEncryption(password.userName)
        encryptedPassword.password = aes.singleEncryption(password.password)
        // Encrypt any other user properties as needed

        return encryptedPassword
    }
}