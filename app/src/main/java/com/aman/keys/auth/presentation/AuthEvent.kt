package com.aman.keys.auth.presentation

sealed class AuthEvent {
    data class GiveAuthorizationAccess(val deviceID:String):AuthEvent()
    data class RemoveAuthorizationAccess(val deviceID:String):AuthEvent()

    object CancelAuthorizationAccessProcess:AuthEvent()

    object GrantAccessPermission:AuthEvent()
    object DeclineAuthorizationAccessProcess:AuthEvent()

}