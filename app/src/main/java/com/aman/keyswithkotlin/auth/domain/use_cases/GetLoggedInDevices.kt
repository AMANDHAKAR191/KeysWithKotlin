package com.aman.keyswithkotlin.auth.domain.use_cases

import com.aman.keyswithkotlin.auth.domain.model.DeviceData
import com.aman.keyswithkotlin.auth.domain.repository.AuthRepository
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.passwords.domain.model.Password
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLoggedInDevices @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<Response<Pair<List<DeviceData>?, Boolean?>>> {
        return authRepository.getLoggedInDevices()
            .map { response ->
                when (response) {
                    is Response.Success -> {
                        response.copy(data = response.data?.sortedBy { it.deviceType }?.toMutableList() as List<DeviceData>)
                    }
                    else -> response
                }
            }
    }
}