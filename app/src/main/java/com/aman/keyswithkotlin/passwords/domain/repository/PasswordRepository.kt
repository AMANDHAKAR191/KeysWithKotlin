package com.aman.keyswithkotlin.passwords.domain.repository

import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.model.RealtimeModelResponse
import kotlinx.coroutines.flow.Flow

interface PasswordRepository {

    fun getPasswords(): Flow<Response<List<Password>>>

    fun insertPassword(password: Password): Flow<Response<String>>

    fun deletePassword(password: Password): Flow<Response<String>>
}