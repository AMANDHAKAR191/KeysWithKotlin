package com.aman.keyswithkotlin.autofill_service

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.passwords.domain.model.InvalidPasswordException
import com.aman.keyswithkotlin.passwords.domain.use_cases.PasswordUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveAutofillPasswordViewModel @Inject constructor(
    private val passwordUseCases: PasswordUseCases,
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<AutofillPasswordEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val _state = mutableStateOf(AutofillPasswordState())
    val state: State<AutofillPasswordState> = _state


    fun onEvent(event: AutofillPasswordEvent) {
        when (event) {
            is AutofillPasswordEvent.SavePassword -> {
                println("viewmodel: check2")
                viewModelScope.launch {
                    try {
                        passwordUseCases.addPassword(event.password).collect { response ->
                            when (response) {
                                is Response.Failure -> {
                                    _eventFlow.emit(
                                        AutofillPasswordEvent.PasswordNotSaved(
                                            response.e.message ?: "An Unexpected error occurred"
                                        )
                                    )
                                }
                                Response.Loading -> {

                                }
                                is Response.Success -> {
                                    println("viewmodel: check6")
                                    _eventFlow.emit(AutofillPasswordEvent.PasswordSaved)
                                }
                            }
                        }
                    }catch (e:InvalidPasswordException){
                        e.printStackTrace()
                    }
                }
            }

            else -> {}
        }
    }
}