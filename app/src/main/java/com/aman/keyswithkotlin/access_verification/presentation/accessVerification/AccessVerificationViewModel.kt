package com.aman.keyswithkotlin.access_verification.presentation.accessVerification

import UIEvents
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.Keys
import com.aman.keyswithkotlin.access_verification.domain.use_cases.AccessVerificationUseCases
import com.aman.keyswithkotlin.auth.domain.model.RequestAuthorizationAccess
import com.aman.keyswithkotlin.core.Authorization
import com.aman.keyswithkotlin.core.DeviceInfo
import com.aman.keyswithkotlin.core.MyPreference
import com.aman.keyswithkotlin.core.util.Response
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
class AccessVerificationViewModel @Inject constructor(
    private val accessVerificationUseCases: AccessVerificationUseCases,
    private val myPreference: MyPreference
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    val _accessRequestingDeviceId = mutableStateOf("")

    val _authorizationCode = MutableStateFlow<Int>(0)
    val authorizationCode = _authorizationCode.asStateFlow()

    init {
        println("check point 1")
        checkAuthorizationOfDevice()
    }

    fun onEvent(event: AccessVerificationEvent) {
        val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
        println("primary device id11: ${myPreference.primaryUserDeviceId}")
        when (event) {
            AccessVerificationEvent.AskAccessPermission -> {
                viewModelScope.launch {
                    accessVerificationUseCases.requestAuthorizationAccess(
                        primaryDeviceId = myPreference.primaryUserDeviceId!!,
                        authorizationCode = generateAuthorizationCode(),
                        requestingDeviceId = deviceInfo.getDeviceId()
                    ).collect {

                    }
                }
            }

            is AccessVerificationEvent.GrantAccessPermission -> {
                viewModelScope.launch {
                    println("_accessRequestingDeviceId.value: ${_accessRequestingDeviceId.value}")
                    accessVerificationUseCases.giveAuthorizationAccessOfSecondaryDevice(
                        _accessRequestingDeviceId.value
                    ).collect {response->
                        when(response){
                            is Response.Failure -> {


                            }
                            Response.Loading -> {


                            }
                            is Response.Success -> {
                                _eventFlow.emit(UIEvents.NavigateToNextScreen)
                                completeAccessGrantingProcess(deviceInfo.getDeviceId())
                            }
                        }

                    }
                }
            }

            AccessVerificationEvent.CancelAuthorizationAccessProcess -> {
                completeAccessGrantingProcess(myPreference.primaryUserDeviceId!!)
            }
        }
    }

    private fun generateAuthorizationCode(): Int {
        _authorizationCode.update {
            (11..99).random()
        }
        return _authorizationCode.value
    }

    private fun checkAuthorizationOfDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
            accessVerificationUseCases.checkAuthorizationOfDevice(deviceInfo.getDeviceId())
                .collect { response ->
                    withContext(Dispatchers.Main) {
                        when (response) {
                            is Response.Success<*, *> -> {
                                println("isAuthorize??: ${response.data as String}")
                                if ((response.data).equals(Authorization.NotAuthorized.toString())) {
                                    println("check point 2")
                                    _eventFlow.emit(UIEvents.ShowAlertDialog)
                                } else {
                                    println("check point 1")
                                    getAccessRequesterClient()
                                }
                            }

                            is Response.Failure -> {

                            }

                            is Response.Loading -> {

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
                            is Response.Success<*, *> -> {
                                println("response.data: ${response.data as RequestAuthorizationAccess}")
                                if (response.data.requestingAccess) {
                                    _accessRequestingDeviceId.value = response.data.requesterID!!
                                    _eventFlow.emit(UIEvents.ShowAuthorizationAlertDialog(_accessRequestingDeviceId.toString(), response.data.authorizationCode))
                                } else {
                                    _eventFlow.emit(UIEvents.HideAuthorizationAlertDialog)
                                    _eventFlow.emit(UIEvents.NavigateToNextScreen)
                                }
                            }

                            is Response.Failure -> {

                            }

                            is Response.Loading -> {

                            }
                        }
                    }
                }
        }
    }

    private fun completeAccessGrantingProcess(deviceId:String) {
        viewModelScope.launch(Dispatchers.IO) {
            accessVerificationUseCases.completeAuthorizationAccessProcess(deviceId)
                .collect { response ->
                    withContext(Dispatchers.Main) {
                        when (response) {
                            is Response.Success<*, *> -> {
                                _eventFlow.emit(UIEvents.NavigateToNextScreen)
                            }

                            is Response.Failure -> {

                            }

                            is Response.Loading -> {

                            }
                        }
                    }
                }
        }
    }

}