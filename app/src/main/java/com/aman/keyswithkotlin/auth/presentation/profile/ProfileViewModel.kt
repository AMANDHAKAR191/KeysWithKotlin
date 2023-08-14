package com.aman.keyswithkotlin.auth.presentation.profile

import UIEvents
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.Keys
import com.aman.keyswithkotlin.access_verification.domain.use_cases.AccessVerificationUseCases
import com.aman.keyswithkotlin.auth.domain.model.DeviceData
import com.aman.keyswithkotlin.auth.domain.model.RequestAuthorizationAccess
import com.aman.keyswithkotlin.auth.domain.repository.RevokeAccessResponse
import com.aman.keyswithkotlin.auth.domain.repository.SignOutResponse
import com.aman.keyswithkotlin.auth.domain.use_cases.AuthUseCases
import com.aman.keyswithkotlin.auth.presentation.AuthEvent
import com.aman.keyswithkotlin.core.DeviceInfo
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.core.util.Response.Loading
import com.aman.keyswithkotlin.core.util.Response.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val accessVerificationUseCases: AccessVerificationUseCases
) : ViewModel() {
    val displayName get() = authUseCases.displayName
    val photoUrl get() = authUseCases.photoUrl

    private val _state = mutableStateOf(ProfileState())
    val state: State<ProfileState> = _state

    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    var signOutResponse by mutableStateOf<SignOutResponse>(Success(false))
        private set
    var revokeAccessResponse by mutableStateOf<RevokeAccessResponse>(Success(false))
        private set

    val _accessRequestingDeviceId = mutableStateOf("")

    init {
        getAccessRequesterClient()
        viewModelScope.launch {
            authUseCases.getLoggedInDevices().collect { response ->
                when (response) {
                    is Success -> {
//                        println("response.data: ${response.data as List<DeviceData>}")
                        _state.value = state.value.copy(
                            loggedInDeviceList = response.data as List<DeviceData>
                        )
                    }

                    is Response.Failure -> {

                    }

                    Loading -> {

                    }
                }

            }
        }
    }

    fun onEvent(event: AuthEvent) {
        val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
        when (event) {
            is AuthEvent.GiveAuthorizationAccess -> {
                viewModelScope.launch {
                    accessVerificationUseCases.giveAuthorizationAccessOfSecondaryDevice(event.deviceID)
                        .collect {

                        }
                }
            }

            is AuthEvent.RemoveAuthorizationAccess -> {
                viewModelScope.launch {
                    accessVerificationUseCases.removeAuthorizationAccessOfSecondaryDevice(event.deviceID)
                        .collect {

                        }
                }
            }

            AuthEvent.GrantAccessPermission -> {
                viewModelScope.launch {
                    println("_accessRequestingDeviceId.value: ${_accessRequestingDeviceId.value}")
                    accessVerificationUseCases.giveAuthorizationAccessOfSecondaryDevice(
                        _accessRequestingDeviceId.value
                    ).collect { response ->
                        when (response) {
                            is Response.Failure -> {


                            }

                            Loading -> {


                            }

                            is Success -> {
                                _eventFlow.emit(UIEvents.NavigateToNextScreen)
                                val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
                                completeAccessGrantingProcess(deviceInfo.getDeviceId())
                            }
                        }

                    }
                }
            }
            AuthEvent.DeclineAuthorizationAccessProcess -> {
                completeAccessGrantingProcess(deviceInfo.getDeviceId())
//                viewModelScope.launch(Dispatchers.IO) {
//                    accessVerificationUseCases.cancelAuthorizationAccessProcess(deviceInfo.getDeviceId())
//                        .collect { response ->
//                            withContext(Dispatchers.Main) {
//                                when (response) {
//                                    is Success<*, *> -> {
//                                        _eventFlow.emit(UIEvents.NavigateToNextScreen)
//                                    }
//
//                                    is Response.Failure -> {
//
//                                    }
//
//                                    is Loading -> {
//
//                                    }
//                                }
//                            }
//                        }
//                }
            }

            AuthEvent.CancelAuthorizationAccessProcess -> {
                completeAccessGrantingProcess("aa32850c3b944554")
//                viewModelScope.launch(Dispatchers.IO) {
//                    val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
//                    accessVerificationUseCases.completeAuthorizationAccessProcess("aa32850c3b944554")
//                        .collect { response ->
//                            withContext(Dispatchers.Main) {
//                                when (response) {
//                                    is Success<*, *> -> {
//                                        _eventFlow.emit(UIEvents.NavigateToNextScreen)
//                                    }
//
//                                    is Response.Failure -> {
//
//                                    }
//
//                                    is Loading -> {
//
//                                    }
//                                }
//                            }
//                        }
//                }
            }
        }
    }

    private fun completeAccessGrantingProcess(deviceId:String) {
        viewModelScope.launch(Dispatchers.IO) {
            accessVerificationUseCases.completeAuthorizationAccessProcess(deviceId)
                .collect { response ->
                    withContext(Dispatchers.Main) {
                        when (response) {
                            is Success<*, *> -> {
                                _eventFlow.emit(UIEvents.NavigateToNextScreen)
                            }

                            is Response.Failure -> {

                            }

                            is Loading -> {

                            }
                        }
                    }
                }
        }
    }

    private fun getAccessRequesterClient() {
        viewModelScope.launch(Dispatchers.IO) {
            val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
            accessVerificationUseCases.getAccessRequesterClient(deviceInfo.getDeviceId())
                .collect { response ->
                    withContext(Dispatchers.Main) {
                        when (response) {
                            is Success<*, *> -> {
                                println("response.data: ${response.data as RequestAuthorizationAccess}")
                                if (response.data.requestingAccess) {
                                    _accessRequestingDeviceId.value = response.data.requesterID!!
                                    _eventFlow.emit(UIEvents.ShowAuthorizationAlertDialog)
                                } else {
                                    _eventFlow.emit(UIEvents.HideAuthorizationAlertDialog)
                                    _eventFlow.emit(UIEvents.NavigateToNextScreen)
                                }
                            }

                            is Response.Failure -> {

                            }

                            is Loading -> {

                            }
                        }
                    }
                }
        }
    }

    fun signOut() = viewModelScope.launch {
        signOutResponse = Loading
        authUseCases.signOut().collect { response ->
            signOutResponse = response
        }

    }

    fun revokeAccess() = viewModelScope.launch {
        revokeAccessResponse = Loading
        authUseCases.revokeAccess().collect { response ->
            revokeAccessResponse = response
        }
    }
}