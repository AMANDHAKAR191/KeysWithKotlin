package com.aman.keyswithkotlin.autofill_service

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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AutofillPasswordViewModel @Inject constructor(
    private val passwordUseCases: PasswordUseCases,
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    val _state = mutableStateOf(AutofillPasswordState())
    val state: State<AutofillPasswordState> = _state

    //check this why this _passwords.value is empty
    private val _passwords = MutableStateFlow<List<Password>>(emptyList())

    init {
        getPasswords()
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
                        }
                    }
                }
            }
        }
    }
}