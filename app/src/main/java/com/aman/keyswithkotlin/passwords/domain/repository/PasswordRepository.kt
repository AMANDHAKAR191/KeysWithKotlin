package com.aman.keyswithkotlin.passwords.domain.repository

import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.model.RealtimeModelResponse
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow

interface PasswordRepository {

    suspend fun getPasswords(): Flow<Response<List<RealtimeModelResponse>>>

    suspend fun getPasswordById(id: Int): Password

    suspend fun insertPassword(password: Password):Flow<Response<String>>

    suspend fun deletePassword(password: Password):Flow<Response<String>>
}