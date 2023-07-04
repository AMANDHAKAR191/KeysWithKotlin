package com.aman.keyswithkotlin.passwords.presentation.generate_password

sealed class GeneratePasswordEvent{
    data class ChangeSliderValueChange(val value: Int):GeneratePasswordEvent()
    data class ChangeSwitchValueChange(val value: Boolean, val identifier:String):GeneratePasswordEvent()
    object GeneratePassword:GeneratePasswordEvent()
}