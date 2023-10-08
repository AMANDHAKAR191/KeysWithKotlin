package com.aman.keyswithkotlin.setting.presentation

import UIEvents
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.auth.domain.model.DeviceData
import com.aman.keyswithkotlin.auth.domain.use_cases.AuthUseCases
import com.aman.keyswithkotlin.auth.presentation.profile.ProfileState
import com.aman.keyswithkotlin.core.MyPreference
import com.aman.keyswithkotlin.core.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val myPreference: MyPreference
) : ViewModel() {
    val displayName get() = authUseCases.displayName
    val photoUrl get() = authUseCases.photoUrl
    val email get() = authUseCases.email

    private var _state = MutableStateFlow(SettingState())
    var state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        myPreference.lockAppSelectedOption?.let {
            state.value.lockAppSelectedOption = it
        }
        viewModelScope.launch {
            authUseCases.getLoggedInDevices().collect { response ->
                when (response) {
                    is Response.Success -> {
                        _state.update {
                            it.copy(
                                loggedInDeviceList = response.data as List<DeviceData>
                            )
                        }
                    }

                    is Response.Failure -> {

                    }

                    Response.Loading -> {

                    }
                }

            }
        }
    }

    fun onEvent(event: SettingEvent){
        when(event){
            is SettingEvent.UpdateLockAppSetting -> {
                myPreference.lockAppSelectedOption = event.value
                _state.update { it.copy(lockAppSelectedOption = event.value) }
            }
        }
    }
}