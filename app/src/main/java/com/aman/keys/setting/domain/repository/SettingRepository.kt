package com.aman.keys.setting.domain.repository

import com.aman.keys.core.util.Response
import com.aman.keys.passwords.domain.model.Password
import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    fun storeImportedPasswords(passwordList:List<Password>): Flow<Response<Pair<String?, Boolean?>>>
}