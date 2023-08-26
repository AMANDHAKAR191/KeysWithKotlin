package com.aman.keyswithkotlin.autofill_service

import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.model.RealtimeModelResponse

data class AutofillPasswordState (
    val passwords:List<Password> = emptyList(),
    val error:String?  = "",
    val isLoading:Boolean = false
)