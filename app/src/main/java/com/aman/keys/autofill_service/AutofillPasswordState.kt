package com.aman.keys.autofill_service

import com.aman.keys.passwords.domain.model.Password

data class AutofillPasswordState (
    val passwords:List<Password> = emptyList(),
    val error:String?  = "",
    val isLoading:Boolean = false,
    val autofillPasswordItem:Password? = null
)