package com.aman.keys.passwords.domain.use_cases

import com.aman.keys.core.util.Response
import com.aman.keys.core.util.TimeStampUtil
import com.aman.keys.passwords.domain.model.Password
import com.aman.keys.passwords.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow

class UpdateLastUsedPasswordTimeStamp(
    private val passwordRepository: PasswordRepository
) {
    operator fun invoke(password: Password): Flow<Response<Pair<String?, Boolean?>>> {
        val timeStampUtil = TimeStampUtil()
        val _password = Password(
            userName = password.userName,
            password = password.password,
            websiteName = password.websiteName,
            linkTo = password.linkTo,
            timestamp = password.timestamp,
            lastUsedTimeStamp = timeStampUtil.generateTimestamp()
        )
        return passwordRepository.updateRecentUsedPasswordTimeStamp(_password)
    }
}