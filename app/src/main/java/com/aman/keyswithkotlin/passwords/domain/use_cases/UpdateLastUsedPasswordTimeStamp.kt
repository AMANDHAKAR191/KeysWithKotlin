package com.aman.keyswithkotlin.passwords.domain.use_cases

import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.core.util.TimeStampUtil
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.repository.PasswordRepository
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
            websiteLink = password.websiteLink,
            timestamp = password.timestamp,
            lastUsedTimeStamp = timeStampUtil.generateTimestamp()
        )
        return passwordRepository.updateRecentUsedPasswordTimeStamp(_password)
    }
}