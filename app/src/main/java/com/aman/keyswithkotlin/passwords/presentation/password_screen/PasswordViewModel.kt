package com.aman.keyswithkotlin.passwords.presentation.password_screen

import UIEvents
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.Keys
import com.aman.keyswithkotlin.access_verification.domain.use_cases.AccessVerificationUseCases
import com.aman.keyswithkotlin.core.Authorization
import com.aman.keyswithkotlin.core.DeviceInfo
import com.aman.keyswithkotlin.core.MyPreference
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.core.util.TutorialType
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val passwordUseCases: PasswordUseCases,
    private val accessVerificationUseCases: AccessVerificationUseCases,
    private val myPreference: MyPreference
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _state = MutableStateFlow(PasswordState())
    val state = _state.asStateFlow()

    private val _isTutorialEnabled = MutableStateFlow(String())
    val isTutorialEnabled = _isTutorialEnabled.asStateFlow()

    private val _searchText = MutableStateFlow("")
    private val searchText = _searchText.asSharedFlow()

    private var recentlyDeletedPassword: Password? = null

    private val _passwords = MutableStateFlow<List<Password>>(emptyList())

    val searchedPasswords = searchText
        .combine(_passwords) { text, passwords ->
            if (text.isBlank()) {
                null
            } else {
               try {
                   passwords.filter { it.doesMatchSearchQuery(text) }
               }catch (e:Exception){
                   println("check1")
                   null
               }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _passwords.value
        )

    init {
        _isTutorialEnabled.update { myPreference.isTutorialEnabled }
        checkAuthorizationOfDevice()
        getPasswords()
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
                                if ((response.data).equals(Authorization.NotAuthorized.toString())) {
                                    _eventFlow.emit(UIEvents.ShowAlertDialog)
                                } else {
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
                println("search text: ${event.value}")
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

            PasswordEvent.DisableTutorial -> {
                _isTutorialEnabled.update { myPreference.isTutorialEnabled }
                myPreference.isTutorialEnabled = TutorialType.DISABLED.toString()
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
                            getRecentlyUsedPasswords()
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
        _state.update {
            it.copy(
                recentlyUsedPasswords = state.value.passwords.sortedByDescending { it.timestamp }
            )
        }
    }
}