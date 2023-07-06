package com.aman.keyswithkotlin.passwords.presentation.password_screen

import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.model.RealtimeModelResponse

data class PasswordState (
    val passwords:List<Password> = emptyList(),
    val error:String?  = "",
    val isLoading:Boolean = false
)