package com.aman.keys.setting.domain.use_cases

import com.aman.keys.core.AES
import com.aman.keys.core.util.Response
import com.aman.keys.di.AESKeySpecs
import com.aman.keys.passwords.domain.model.InvalidPasswordException
import com.aman.keys.passwords.domain.model.Password
import com.aman.keys.setting.domain.repository.SettingRepository
import kotlinx.coroutines.flow.Flow

class StoreImportedPasswords(
    private val settingRepository: SettingRepository,
    private val aesCloudKeySpecs: AESKeySpecs,
    private val aesLocalKeySpecs: AESKeySpecs
) {
    @Throws(InvalidPasswordException::class)
    operator fun invoke(passwordList:List<Password>): Flow<Response<Pair<String?, Boolean?>>> {
        println("AddPassword: check3")
        println("aesCloudKeySpecs: ${aesCloudKeySpecs.aesKey} || ${aesCloudKeySpecs.aesIV}")
        println("aesLocalKeySpecs: ${aesLocalKeySpecs.aesKey} || ${aesLocalKeySpecs.aesIV}")

        println("AddPassword: check4")
        val aes = AES.getInstance(aesCloudKeySpecs.aesKey, aesCloudKeySpecs.aesIV)
            ?: throw IllegalStateException("Failed to initialize AES instance.")

        val encryptedPasswordList  =  passwordList.map {password->
            encryptPassword(password, aes)
        }
        return settingRepository.storeImportedPasswords(encryptedPasswordList)
    }

    private fun encryptPassword(password: Password, aes: AES): Password {
        val encryptedPassword = password.copy()
        encryptedPassword.userName = aes.singleEncryption(password.userName)
        encryptedPassword.password = aes.singleEncryption(password.password)
        // Encrypt any other user properties as needed

        return encryptedPassword
    }
}