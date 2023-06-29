package com.aman.keyswithkotlin.passwords.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.passwords.domain.use_cases.PasswordUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val passwordUseCases: PasswordUseCases
) : ViewModel() {

    val _state = mutableStateOf(PasswordState())
    val state: State<PasswordState> = _state

    init {
        println("inside the ViewModel")
        getPasswords()
    }

    private fun getPasswords() {
        viewModelScope.launch(Dispatchers.IO) {
            passwordUseCases.getPasswords().collect { response ->
                println(this.coroutineContext)
                withContext(Dispatchers.Main){
                    println(this.coroutineContext)
                    when (response) {
                        is Response.Success -> {
                            _state.value = PasswordState(
                                passwords = response.data!!
                            )
                        }

                        is Response.Failure -> {
                            _state.value = PasswordState(
                                error = response.e.message
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