package com.aman.keys.access_verification.domain.use_cases

import com.aman.keys.access_verification.domain.repository.AccessVerificationRepository
import com.aman.keys.auth.domain.model.DeviceData
import com.aman.keys.core.Authorization
import com.aman.keys.core.DeviceType
import com.aman.keys.core.MyPreference
import com.aman.keys.core.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CheckAuthorizationOfDevice(
    private val accessVerificationRepository: AccessVerificationRepository,
    private val myPreference: MyPreference
) {
    operator fun invoke(deviceId: String): Flow<Response<Pair<String?, Boolean?>>> = callbackFlow {
        accessVerificationRepository.checkAuthorizationOfDevice(deviceId).collect {
            // Process the authorizationResult here
            val response = processAuthorizationResult(deviceId, it)
            trySend(response)
        }
    }

    private fun processAuthorizationResult(
        deviceId: String,
        authorizationResult: Response<Pair<List<DeviceData>?, Boolean?>>
    ): Response<Pair<String?, Boolean?>> {
        // Perform any necessary processing here
        // For example, you can extract relevant data or perform error handling
        // Replace the placeholders below with actual logic
        return when (authorizationResult) {
            is Response.Failure -> {
                Response.Failure(Throwable("Authorization failed")) // Handle error case appropriately
            }

            is Response.Loading -> {
                Response.Loading(message = null)
            }

            is Response.Success -> {
                var responseString: String = Authorization.NotAuthorized.toString()
                authorizationResult.data?.forEach {
                    if (it.deviceType == DeviceType.Primary.toString()) {
                        println("primary device id: ${it.deviceId}")
                        myPreference.primaryUserDeviceId = it.deviceId
                    }
                    if (it.deviceId == deviceId) {
                        responseString = if (it.authorization == Authorization.Authorized.toString()) {
                            Authorization.Authorized.toString()
                        } else {
                            Authorization.NotAuthorized.toString()
                        }
                    }
                }
                Response.Success(responseString)
            }
        }
    }
}
