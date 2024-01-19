package com.aman.keys.autofill_service

import com.aman.keys.passwords.domain.model.Password

sealed class AutofillPasswordEvent {
    data class SavePassword(val password: Password): AutofillPasswordEvent()
    object PasswordSaved:AutofillPasswordEvent()
    data class PasswordNotSaved(val error:String):AutofillPasswordEvent()
}