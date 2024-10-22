package com.aman.keys.passwords.domain.repository

import com.aman.keys.core.util.Response
import com.aman.keys.passwords.domain.model.GeneratedPasswordModelClass
import com.aman.keys.passwords.domain.model.Password
import kotlinx.coroutines.flow.Flow

interface PasswordRepository {

    fun getPasswords(): Flow<Response<Pair<MutableList<Password>?, Boolean?>>>
    fun getRecentlyUsedPasswords(): Flow<Response<Pair<MutableList<Password>?, Boolean?>>>

    fun insertPassword(password: Password): Flow<Response<Pair<String?, Boolean?>>>
    fun deletePassword(password: Password): Flow<Response<Pair<String?, Boolean?>>>

    fun saveGeneratedPassword(generatePassword: String, recentPasswordsList: MutableList<GeneratedPasswordModelClass>): Flow<Response<Pair<String?, Boolean?>>>

    fun getRecentGeneratedPasswords(): Flow<Response<Pair<MutableList<GeneratedPasswordModelClass>?, Boolean?>>>
    fun updateRecentUsedPasswordTimeStamp(password: Password): Flow<Response<Pair<String?, Boolean?>>>

//    fun checkAuthorizationOfDevice(deviceId:String):Flow<Response<Pair<String?, Boolean?>>>
}