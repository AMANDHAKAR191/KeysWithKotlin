package com.aman.keyswithkotlin.passwords.presentation.password_screen

import UIEvents
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.Keys
import com.aman.keyswithkotlin.access_verification.domain.use_cases.AccessVerificationUseCases
import com.aman.keyswithkotlin.auth.domain.model.RequestAuthorizationAccess
import com.aman.keyswithkotlin.core.Authorization
import com.aman.keyswithkotlin.core.DeviceInfo
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.use_cases.PasswordUseCases
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.PasswordEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val passwordUseCases: PasswordUseCases,
    private val accessVerificationUseCases: AccessVerificationUseCases
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    val _state = MutableStateFlow(PasswordState())
    val state= _state.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asSharedFlow()

    private var recentlyDeletedPassword: Password? = null

    //check this why this _passwords.value is empty
    private val _passwords = MutableStateFlow<List<Password>>(emptyList())

    val searchedPasswords = searchText
        .combine(_passwords) { text, passwords ->
            if (text.isBlank()) {
                null
            } else {
                passwords.filter { it.doesMatchSearchQuery(text) }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _passwords.value
        )

    init {
        checkAuthorizationOfDevice()
        getPasswords()
        getRecentlyUsedPasswords()
    }

    private fun checkAuthorizationOfDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
            accessVerificationUseCases.checkAuthorizationOfDevice(deviceInfo.getDeviceId())
                .collect { response ->
                    withContext(Dispatchers.Main) {
                        when (response) {
                            is Response.Success<*, *> -> {
                                println("isAuthorize: ${response.data as String}")
                                if ((response.data).equals(Authorization.NotAuthorized.toString())){
                                    _eventFlow.emit(UIEvents.ShowAlertDialog)
                                }else{
                                    _eventFlow.emit(UIEvents.HideAlertDialog)
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

    fun onEvent(event: PasswordEvent) {
        when (event) {
            is PasswordEvent.RestorePassword -> {
                viewModelScope.launch {
                    passwordUseCases.addPassword(recentlyDeletedPassword!!)
                        .collect {
                            recentlyDeletedPassword = null
                        }
                }
            }

            is PasswordEvent.DeletePassword -> {
                viewModelScope.launch {
                    passwordUseCases.deletePassword(event.password).collect { response ->
                        when (response) {
                            is Response.Success<*, *> -> {
                                recentlyDeletedPassword = event.password
                                _eventFlow.emit(
                                    UIEvents.ShowSnackBar(
                                        "Password deleted",
                                        true,
                                        "Restore"
                                    )
                                )
                            }

                            else -> {}
                        }
                    }
                }
            }

            is PasswordEvent.OnSearchTextChange -> {
                _searchText.value = event.value
            }

            is PasswordEvent.UpdateLastUsedPasswordTimeStamp -> {
                viewModelScope.launch {
                    passwordUseCases.updateLastUsedPasswordTimeStamp(event.password)
                        .collect { response ->
                            when (response) {
                                is Response.Success<*, *> -> {

                                }

                                is Response.Failure -> {

                                }

                                is Response.Loading -> {

                                }
                            }
                        }
                }
            }

            else -> {}
        }
    }

    private fun getPasswords() {
        viewModelScope.launch(Dispatchers.IO) {
            passwordUseCases.getPasswords().collect { response ->
                withContext(Dispatchers.Main) {
                    when (response) {
                        is Response.Success<*, *> -> {
                            _state.value = state.value.copy(
                                passwords = response.data as List<Password>,
                                isLoading = false
                            )
                            _passwords.value = response.data
                        }

                        is Response.Failure -> {
                            _state.value = state.value.copy(
                                error = response.e.message ?: "Unexpected error occurred",
                                isLoading = false
                            )
                        }

                        is Response.Loading -> {
                            _state.value = PasswordState(
                                isLoading = true
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getRecentlyUsedPasswords() {
        /*       this Unconfined Dispatchers added because
                 if i use same io Dispatchers then either
                 one of the data is loading other data couldn't loaded */
        viewModelScope.launch(Dispatchers.Unconfined) {
            passwordUseCases.getRecentlyUsedPasswords().collect { response ->
                withContext(Dispatchers.Main) {
                    when (response) {
                        is Response.Success<*, *> -> {
                            _state.value = state.value.copy(
                                recentlyUsedPasswords = response.data as List<Password>,
                                isLoading = false
                            )
                        }

                        is Response.Failure -> {
                            _state.value = state.value.copy(
                                error = response.e.message ?: "Unexpected error occurred",
                                isLoading = false
                            )
                        }

                        is Response.Loading -> {
                            _state.value = PasswordState(
                                isLoading = true
                            )
                        }
                    }
                }
            }
        }
    }
}