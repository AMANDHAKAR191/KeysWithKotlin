package com.aman.keys.auth.presentation.profile

import UIEvents
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keys.Keys
import com.aman.keys.access_verification.domain.use_cases.AccessVerificationUseCases
import com.aman.keys.auth.domain.model.RequestAuthorizationAccess
import com.aman.keys.auth.domain.repository.RevokeAccessResponse
import com.aman.keys.auth.domain.repository.SignOutResponse
import com.aman.keys.auth.domain.use_cases.AuthUseCases
import com.aman.keys.auth.presentation.AuthEvent
import com.aman.keys.core.DeviceInfo
import com.aman.keys.core.MyPreference
import com.aman.keys.core.util.Response
import com.aman.keys.core.util.Response.Loading
import com.aman.keys.core.util.Response.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val accessVerificationUseCases: AccessVerificationUseCases,
    private val myPreference: MyPreference
) : ViewModel() {
    val displayName get() = authUseCases.displayName
    val photoUrl get() = authUseCases.photoUrl

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    var signOutResponse by mutableStateOf<SignOutResponse>(Success(false))
        private set
    var revokeAccessResponse by mutableStateOf<RevokeAccessResponse>(Success(false))
        private set

    val _accessRequestingDeviceId = mutableStateOf("")
    val _authorizationCode = mutableIntStateOf(0)

    init {
        getAccessRequesterClient()
        loggedInDeviceList()
    }

    private fun loggedInDeviceList() {
        viewModelScope.launch {
            authUseCases.getLoggedInDevices().collect { response ->
                when (response) {
                    is Success -> {
                        _state.update {
                            it.copy(
//                                loggedInDeviceList = response.data as List<DeviceData>
                            )
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
                    accessVerificationUseCases.giveAuthorizationAccessOfSecondaryDevice(
                        _accessRequestingDeviceId.value
                    ).collect { response ->
                        when (response) {
                            is Response.Failure -> {


                            }

                            is Loading -> {


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
            }

            AuthEvent.CancelAuthorizationAccessProcess -> {
                completeAccessGrantingProcess(myPreference.primaryUserDeviceId!!)
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
                            is Success<RequestAuthorizationAccess, *> -> {
                                if (response.data?.requestingAccess == true) {
                                    _accessRequestingDeviceId.value = response.data.requesterID!!
                                    _authorizationCode.value = response.data.authorizationCode
                                    _eventFlow.emit(UIEvents.ShowAuthorizationAlertDialog(_accessRequestingDeviceId.toString(), _authorizationCode.value))
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
        signOutResponse = Loading(message = null)
        authUseCases.signOut().collect { response ->
            signOutResponse = response
        }

    }

    fun revokeAccess() = viewModelScope.launch {
        revokeAccessResponse = Loading(message = null)
        authUseCases.revokeAccess().collect { response ->
            revokeAccessResponse = response
        }
    }
}