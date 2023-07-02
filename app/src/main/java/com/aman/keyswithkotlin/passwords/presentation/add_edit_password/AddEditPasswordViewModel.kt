package com.aman.keyswithkotlin.passwords.presentation.add_edit_password

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.passwords.domain.model.InvalidPasswordException
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.use_cases.PasswordUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditPasswordViewModel @Inject constructor(
    private val passwordUseCases: PasswordUseCases
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _userName = mutableStateOf(
        TextFieldState(
            hint = "User name"
        )
    )
    val userName: State<TextFieldState> = _userName

    private val _password = mutableStateOf(
        TextFieldState(
            hint = "Password"
        )
    )
    val password: State<TextFieldState> = _password

    private val _websiteName = mutableStateOf(
        TextFieldState(
            hint = "Website name"
        )
    )
    val websiteName: State<TextFieldState> = _websiteName
    private val _websiteLink = mutableStateOf(
        TextFieldState(
            hint = "Website Link (optional)"
        )
    )
    val websiteLink: State<TextFieldState> = _websiteLink

    fun onEvent(event: PasswordEvent) {
        when (event) {
            is PasswordEvent.EnteredUsername -> {
                _userName.value = userName.value.copy(
                    text = event.value
                )
            }

            is PasswordEvent.EnteredPassword -> {
                _password.value = password.value.copy(
                    text = event.value
                )
            }

            is PasswordEvent.EnteredWebsiteName -> {
                _websiteName.value = websiteName.value.copy(
                    text = event.value
                )
            }

            is PasswordEvent.SavePassword -> {
                viewModelScope.launch {
                    println()
                    try {
                        passwordUseCases.addPassword(
                            Password(
                                userName = userName.value.text,
                                password = password.value.text,
                                websiteName = websiteName.value.text,
                                websiteLink = ""
                            )
                        ).collect { response ->
                            when (response) {
                                is Response.Loading -> {

                                }

                                is Response.Success -> {
                                    _eventFlow.emit(
                                        UiEvent.ShowSnackBar(
                                            message = response.data.toString()
                                        )
                                    )
                                }

                                is Response.Failure -> {

                                }
                            }
                            _eventFlow.emit(
                                UiEvent.savePassword
                            )
                        }

                    } catch (e: InvalidPasswordException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackBar(
                                message = e.message ?: "Couldn't save Expense"
                            )
                        )
                    }
                }
            }

            else -> {}
        }
    }

    sealed class UiEvent {
        data class ShowSnackBar(val message: String) : UiEvent()
        object savePassword : UiEvent()
    }
}