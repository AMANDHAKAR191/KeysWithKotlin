package com.aman.keyswithkotlin.passwords.domain.use_cases

data class PasswordUseCases(
    val addPassword: AddPassword,
    val deletePassword: DeletePassword,
    val getPassword: GetPassword,
    val getPasswords: GetPasswords
)