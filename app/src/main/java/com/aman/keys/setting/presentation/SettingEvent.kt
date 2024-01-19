package com.aman.keys.setting.presentation

import com.aman.keys.passwords.domain.model.Password

sealed class SettingEvent {
    data class UpdateLockAppSetting(val value:String): SettingEvent()
    data class StoreImportedPasswords(val passwordList:List<Password>):SettingEvent()

    object EnableTutorial: SettingEvent()

}