package com.aman.keyswithkotlin.setting.presentation

import com.aman.keyswithkotlin.passwords.domain.model.Password

sealed class SettingEvent {
    data class UpdateLockAppSetting(val value:String): SettingEvent()
}