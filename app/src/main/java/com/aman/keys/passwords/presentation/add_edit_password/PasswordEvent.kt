package com.aman.keys.passwords.presentation.add_edit_password

import com.aman.keys.passwords.domain.model.Password

sealed class PasswordEvent {
    data class EnteredUsername(val value:String): PasswordEvent()
    data class EnteredPassword(val value:String): PasswordEvent()
    data class EnteredWebsiteName(val value:String): PasswordEvent()

    data class OnSearchTextChange(val value: String):PasswordEvent()

    object SavePassword: PasswordEvent()

    object DisableTutorial:PasswordEvent()

    data class UpdateLastUsedPasswordTimeStamp(val password: Password):PasswordEvent()
    object RestorePassword : PasswordEvent()
    data class DeletePassword(val password: Password) : PasswordEvent()
}