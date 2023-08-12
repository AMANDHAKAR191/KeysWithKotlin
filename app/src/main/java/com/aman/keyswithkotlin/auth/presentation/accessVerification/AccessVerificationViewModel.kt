package com.aman.keyswithkotlin.auth.presentation.accessVerification

import UIEvents
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.Keys
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
    private val authUseCases: AuthUseCases
):ViewModel() {
    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow.asSharedFlow()
    init {
        checkAuthorizationOfDevice()
    }

    private fun checkAuthorizationOfDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
            authUseCases.checkAuthorizationOfDevice(deviceInfo.getDeviceId())
                .collect { response ->
                    withContext(Dispatchers.Main) {
                        when (response) {
                            is Response.Success<*, *> -> {
                                println("isAuthorize: ${response.data as String}")
                                if ((response.data).equals(Authorization.NotAuthorize.toString())){
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