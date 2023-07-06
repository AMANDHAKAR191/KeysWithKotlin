package com.aman.keyswithkotlin.passwords.presentation.generate_password

import androidx.compose.ui.platform.ClipboardManager

sealed class GeneratePasswordEvent{
    data class ChangeSliderValueChange(val value: Int):GeneratePasswordEvent()
    data class ChangeSwitchValueChange(val value: Boolean, val identifier:String):GeneratePasswordEvent()

    data class CopyPassword(val clipboardManager: ClipboardManager):GeneratePasswordEvent()
    object GeneratePassword:GeneratePasswordEvent()
}