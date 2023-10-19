package com.aman.keyswithkotlin.passwords.presentation.add_edit_password

import UIEvents
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
    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _state = mutableStateOf(AddEditPasswordState())
    val state:State<AddEditPasswordState> = _state

    fun onEvent(event: PasswordEvent) {
        when (event) {
            is PasswordEvent.EnteredUsername -> {
                _state.value = state.value.copy(
                    username = event.value
                )
            }

            is PasswordEvent.EnteredPassword -> {
                _state.value = state.value.copy(
                    password = event.value
                )
            }

            is PasswordEvent.EnteredWebsiteName -> {
                _state.value = state.value.copy(
                    websiteName = event.value
                )
            }

            is PasswordEvent.SavePassword -> {
                viewModelScope.launch {
                    try {
                        passwordUseCases.addPassword(
                            Password(
                                userName = state.value.username,
                                password = state.value.password,
                                websiteName = state.value.websiteName,
                                linkTo = emptyList()
                            )
                        ).collect { response ->
                            when (response) {
                                is Response.Loading -> {

                                }

                                is Response.Success<*, *> -> {
                                    _eventFlow.emit(
                                        UIEvents.ShowSnackBar(
                                            message = response.data.toString()
                                        )
                                    )
                                }

                                is Response.Failure -> {

                                }
                            }
                            _eventFlow.emit(
                                UIEvents.SavePassword
                            )
                        }

                    } catch (e: InvalidPasswordException) {
                        _eventFlow.emit(
                            UIEvents.ShowSnackBar(
                                message = e.message ?: "Couldn't save Expense"
                            )
                        )
                    }
                }
            }

            else -> {}
        }
    }

}