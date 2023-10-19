package com.aman.keyswithkotlin.setting.presentation

import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.PasswordEvent

sealed class SettingEvent {
    data class UpdateLockAppSetting(val value:String): SettingEvent()
    data class StoreImportedPasswords(val passwordList:List<Password>):SettingEvent()

    object EnableTutorial: SettingEvent()

}