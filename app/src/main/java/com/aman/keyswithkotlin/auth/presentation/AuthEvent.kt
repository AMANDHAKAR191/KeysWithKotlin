package com.aman.keyswithkotlin.auth.presentation

import com.aman.keyswithkotlin.access_verification.presentation.accessVerification.AccessVerificationEvent

sealed class AuthEvent {
    data class GiveAuthorizationAccess(val deviceID:String):AuthEvent()
    data class RemoveAuthorizationAccess(val deviceID:String):AuthEvent()

    object CancelAuthorizationAccessProcess:AuthEvent()

    object GrantAccessPermission:AuthEvent()
    object DeclineAuthorizationAccessProcess:AuthEvent()

}