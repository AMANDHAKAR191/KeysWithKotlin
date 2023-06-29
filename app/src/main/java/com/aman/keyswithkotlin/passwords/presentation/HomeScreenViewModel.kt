package com.aman.keyswithkotlin.passwords.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.model.RealtimeModelResponse
import com.aman.keyswithkotlin.passwords.domain.use_cases.PasswordUseCases
import com.aman.keyswithkotlin.core.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val passwordUseCases: PasswordUseCases
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _state: MutableState<ItemState> = mutableStateOf(ItemState())
    val state: State<ItemState> = _state

    init {
        viewModelScope.launch {
            passwordUseCases.getPasswords().collect {
                when (it) {
                    is Response.Success -> {
                        _state.value = ItemState(
                            item = it.data
                        )
                    }

                    is Response.Failure -> {
                        _state.value = ItemState(
                            error = it.e.toString()
                        )
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = "Error: ${it.e.message}"
                            )
                        )
                    }

                    is Response.Loading -> {
                        _state.value = ItemState(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditPasswordEvent) {
        when (event) {
            is AddEditPasswordEvent.SavePassword -> {
                println("check1")
                viewModelScope.launch {
                    try {
                        viewModelScope.launch {
                            passwordUseCases.addPassword(
                                Password(
                                    userName = "AMAN",
                                    password = "dsfdfdfgdgffrhfgh",
                                    websiteName = "aman",
                                    websiteLink = "fvdvdfgvfdgv"
                                )
                            ).collect {
                                _eventFlow.emit(
                                    UiEvent.ShowSnackbar(
                                        message = "Password saved",
                                    )
                                )
                            }
                        }
                        _eventFlow.emit(UiEvent.SavePassword)

                    } catch (e: Exception) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = e.message ?: "Couldn't save password"
                            )
                        )
                    }
                }
            }

            else -> {}
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SavePassword : UiEvent()
    }
}

data class ItemState(
    val item: List<RealtimeModelResponse>? = emptyList(),
    val error: String? = "",
    val isLoading: Boolean = false
)
