package com.aman.keyswithkotlin.passwords.presentation

import com.aman.keyswithkotlin.passwords.domain.model.Password

data class PasswordState(
    val passwords: List<Password> = emptyList()
)