package com.aman.keyswithkotlin.access_verification.presentation.accessVerification

import UIEvents
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.Keys
import com.aman.keyswithkotlin.access_verification.domain.use_cases.AccessVerificationUseCases
import com.aman.keyswithkotlin.auth.domain.use_cases.AuthUseCases
import com.aman.keyswithkotlin.core.Authorization
import com.aman.keyswithkotlin.core.DeviceInfo
import com.aman.keyswithkotlin.core.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccessVerificationViewModel @Inject constructor(
    private val accessVerificationUseCases: AccessVerificationUseCases
):ViewModel() {
    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow.asSharedFlow()
    init {
        println("check1")
        checkAuthorizationOfDevice()
    }

    private fun checkAuthorizationOfDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
            println("check2")
            accessVerificationUseCases.checkAuthorizationOfDevice(deviceInfo.getDeviceId())
                .collect { response ->
                    withContext(Dispatchers.Main) {
                        when (response) {
                            is Response.Success<*, *> -> {
                                println("isAuthorize: ${response.data as String}")
                                if ((response.data).equals(Authorization.NotAuthorized.toString())){
                                    _eventFlow.emit(UIEvents.ShowAlertDialog)
                                }else{
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

}