package com.aman.keyswithkotlin.passwords.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.passwords.domain.use_cases.PasswordUseCases
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.PasswordEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val passwordUseCases: PasswordUseCases
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<AddEditPasswordViewModel.UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val _state = mutableStateOf(PasswordState())
    val state: State<PasswordState> = _state


    init {
        println("inside the ViewModel")
        getPasswords()
    }

    fun onEvent(event: PasswordEvent){
        when(event){
            is PasswordEvent.RestorePassword->{
                viewModelScope.launch {
                    passwordUseCases.addPassword(event.password)
                        .collect{response->
                        when (response) {
                            is Response.Success -> {
                                println("check: password deleted")
//                                _eventFlow.emit(
//                                    AddEditPasswordViewModel.UiEvent.ShowSnackBar(
//                                        message = "Password restored"
//                                    )
//                                )
                            }

                            else -> {}
                        }
                    }
                }
            }
            is PasswordEvent.DeletePassword->{
                viewModelScope.launch {
                    passwordUseCases.deletePassword(event.password).collect{response->
                        println("check1: password deleted $response")
                        when (response) {
                            is Response.Success -> {
                                println("check: password deleted")
                                _eventFlow.emit(
                                    AddEditPasswordViewModel.UiEvent.ShowSnackBar(
                                        message = response.data!!
                                    )
                                )
                            }

                            else -> {}
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
                println(this.coroutineContext)
                withContext(Dispatchers.Main){
                    println(this.coroutineContext)
                    when (response) {
                        is Response.Success -> {
                            _state.value = state.value.copy(
                                passwords = response.data!!,
                                isLoading = false
                            )
                        }

                        is Response.Failure -> {
                            _state.value = state.value.copy(
                                error  = response.e.message ?: "Unexpected error occurred",
                                isLoading = false
                            )
                        }

                        is Response.Loading -> {
                            _state.value = PasswordState(
                                isLoading = true
                            )
                        }
                    }
                    println("password: ${state.value.passwords}")
                }
            }
        }
    }
}