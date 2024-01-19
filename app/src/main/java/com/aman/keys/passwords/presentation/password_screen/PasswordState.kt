package com.aman.keys.passwords.presentation.password_screen

import com.aman.keys.passwords.domain.model.Password

data class PasswordState (
    val passwords:List<Password> = emptyList(),
    val recentlyUsedPasswords:List<Password> = emptyList(),
    val error:String?  = "",
    val isLoading:Boolean = false,
)