package com.aman.keyswithkotlin.passwords.domain.use_cases

data class PasswordUseCases(
    val addPassword: AddPassword,
    val deletePassword: DeletePassword,
    val getPasswords: GetPasswords
)