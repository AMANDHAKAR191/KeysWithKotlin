package com.aman.keyswithkotlin.access_verification.presentation.accessVerification

import com.aman.keyswithkotlin.auth.presentation.AuthEvent

sealed class AccessVerificationEvent  {
    object AskAccessPermission:AccessVerificationEvent()
    object GrantAccessPermission:AccessVerificationEvent()

    object CancelAuthorizationAccessProcess: AccessVerificationEvent()
}