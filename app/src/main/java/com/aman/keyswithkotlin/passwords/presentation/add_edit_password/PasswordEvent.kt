package com.aman.keyswithkotlin.passwords.presentation.add_edit_password

import com.aman.keyswithkotlin.passwords.domain.model.Password

sealed class PasswordEvent {
    data class EnteredUsername(val value:String): PasswordEvent()
    data class EnteredPassword(val value:String): PasswordEvent()
    data class EnteredWebsiteName(val value:String): PasswordEvent()

    data class OnSearchTextChange(val value: String):PasswordEvent()

    object SavePassword: PasswordEvent()
    data class RestorePassword(val password: Password) : PasswordEvent()
    data class DeletePassword(val password: Password) : PasswordEvent()
}