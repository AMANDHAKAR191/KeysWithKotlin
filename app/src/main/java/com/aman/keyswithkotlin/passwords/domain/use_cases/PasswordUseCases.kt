package com.aman.keyswithkotlin.passwords.domain.use_cases

data class PasswordUseCases(
    val addPassword: AddPassword,
    val deletePassword: DeletePassword,
    val getPasswords: GetPasswords,
    val getRecentlyUsedPasswords: GetRecentlyUsedPasswords,
    val updateLastUsedPasswordTimeStamp: UpdateLastUsedPasswordTimeStamp,
    val generatePassword: GeneratePassword,
    val saveRecentGeneratedPassword: SaveRecentGeneratedPassword,
    val getRecentGeneratedPasswords: GetRecentGeneratedPasswords,
    val checkAuthorizationOfDevice: CheckAuthorizationOfDevice
)