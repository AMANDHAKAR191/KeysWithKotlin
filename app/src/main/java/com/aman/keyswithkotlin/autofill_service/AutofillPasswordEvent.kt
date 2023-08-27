package com.aman.keyswithkotlin.autofill_service

import com.aman.keyswithkotlin.passwords.domain.model.Password

sealed class AutofillPasswordEvent {
    data class SavePassword(val password: Password): AutofillPasswordEvent()
    object PasswordSaved:AutofillPasswordEvent()
    data class PasswordNotSaved(val error:String):AutofillPasswordEvent()
}