package com.aman.keys.setting.presentation.manage_devicces

sealed class ManageDevicesEvent {
    data class GiveAuthorizationAccess(val deviceID:String):ManageDevicesEvent()
    data class RemoveAuthorizationAccess(val deviceID:String):ManageDevicesEvent()

    object CancelAuthorizationAccessProcess:ManageDevicesEvent()

    object GrantAccessPermission:ManageDevicesEvent()
    object DeclineAuthorizationAccessProcess:ManageDevicesEvent()

}