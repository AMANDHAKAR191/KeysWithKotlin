package com.aman.keys.access_verification.presentation.accessVerification

sealed class AccessVerificationEvent  {
    object AskAccessPermission:AccessVerificationEvent()
    object GrantAccessPermission:AccessVerificationEvent()

    object CancelAuthorizationAccessProcess: AccessVerificationEvent()
}