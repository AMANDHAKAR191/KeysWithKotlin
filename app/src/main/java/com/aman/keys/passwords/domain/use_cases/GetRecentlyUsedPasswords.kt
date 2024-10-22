package com.aman.keys.passwords.domain.use_cases

import com.aman.keys.core.AES
import com.aman.keys.core.util.Response
import com.aman.keys.di.AESKeySpecs
import com.aman.keys.passwords.domain.model.Password
import com.aman.keys.passwords.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetRecentlyUsedPasswords(
    private val passwordRepository: PasswordRepository,
    private val aesCloudKeySpecs: AESKeySpecs,
    private val aesLocalKeySpecs: AESKeySpecs
) {
    operator fun invoke(): Flow<Response<Pair<MutableList<Password>?, Boolean?>>> {
        val aes = AES.getInstance(aesCloudKeySpecs.aesKey, aesCloudKeySpecs.aesIV)
            ?: throw IllegalStateException("Failed to initialize AES instance.")
        return passwordRepository.getRecentlyUsedPasswords()
            .map { response ->
                when (response) {
                    is Response.Success -> {
                        println("response1: ${response.data}")
                        val decryptedPasswords = response.data?.map { encryptedPassword ->
                            if (encryptedPassword.userName.isBlank()) {
                                encryptedPassword
                            } else {
                                decryptPassword(encryptedPassword, aes)
                            }
                        }
                        response.copy(data = decryptedPasswords?.sortedByDescending { it.lastUsedTimeStamp }?.toMutableList() as MutableList<Password>?)
                    }
                    else -> response
                }
            }
    }

    private fun decryptPassword(encryptedPassword: Password, aes: AES): Password {
        val password = encryptedPassword.copy()
        password.userName = aes.singleDecryption(encryptedPassword.userName)
        password.password = aes.singleDecryption(encryptedPassword.password)
        // Decrypt any other user properties as needed

        return password
    }
}
