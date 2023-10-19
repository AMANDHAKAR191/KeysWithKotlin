package com.aman.keyswithkotlin.setting.domain.repository

import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.passwords.domain.model.Password
import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    fun storeImportedPasswords(passwordList:List<Password>): Flow<Response<Pair<String?, Boolean?>>>
}