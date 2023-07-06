package com.aman.keyswithkotlin.passwords.presentation.add_edit_password

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShareGeneratedPasswordViewModel @Inject constructor(
): ViewModel(){
    private val _generatedPassword = mutableStateOf(SharedPasswordState())
    val generatedPassword:State<SharedPasswordState> = _generatedPassword


    init {
        println("inside: ${generatedPassword.value.generatedPassword}")
    }

    fun onEvent(event: SharedPasswordEvent){
        when(event){
            is SharedPasswordEvent.onPasswordGenerated ->{
                println("inside: SharedViewModelPassword ${event.value}")
                _generatedPassword.value = generatedPassword.value.copy(
                    generatedPassword = event.value
                )
            }
        }
    }

}

sealed class SharedPasswordEvent{
    data class onPasswordGenerated(val value:String): SharedPasswordEvent()
}

data class SharedPasswordState(
    var generatedPassword:String = ""
)