package com.aman.keyswithkotlin.passwords.presentation.add_edit_password

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.aman.keyswithkotlin.passwords.domain.model.Password
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharePasswordViewModel @Inject constructor(
) : ViewModel() {
    private val _generatedPassword = mutableStateOf(SharedPasswordState())
    val generatedPassword: State<SharedPasswordState> = _generatedPassword

    private val _itemToEdit = mutableStateOf(SharedPasswordState())
    val itemToEdit: State<SharedPasswordState> = _itemToEdit

    private val _itemToShare = mutableStateOf(SharedPasswordState())
    val itemToShare: State<SharedPasswordState> = _itemToShare


    init {
        println("inside: ${generatedPassword.value.generatedPassword}")
    }

    fun onEvent(event: SharedPasswordEvent){
        when(event){
            is SharedPasswordEvent.onPasswordGenerated ->{
                _generatedPassword.value = generatedPassword.value.copy(
                    generatedPassword = event.value
                )
            }
            is SharedPasswordEvent.onEditItem->{
                _itemToEdit.value = itemToEdit.value.copy(
                    passwordItem = event.value
                )
            }
            is SharedPasswordEvent.resetViewmodel->{
                _generatedPassword.value = generatedPassword.value.copy(
                    generatedPassword = ""
                )
                _itemToEdit.value = itemToEdit.value.copy(
                    passwordItem = null
                )
            }
            is  SharedPasswordEvent.onSharePassword->{
                _itemToShare.value = itemToShare.value.copy(
                    passwordItem = event.value
                )
            }
        }
    }
}

sealed class SharedPasswordEvent{
    data class onPasswordGenerated(val value:String): SharedPasswordEvent()
    data class onEditItem(val value: Password):SharedPasswordEvent()

    data class onSharePassword(val value: Password):SharedPasswordEvent()

    object resetViewmodel:SharedPasswordEvent()
}

data class SharedPasswordState(
    var generatedPassword:String = "",
    val passwordItem:Password? = null
)