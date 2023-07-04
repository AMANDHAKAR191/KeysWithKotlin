package com.aman.keyswithkotlin.passwords.presentation.generate_password

data class GeneratePasswordState(
    val generatedPassword:String = "",
    val slider:Int = 4,
    val upperCaseAlphabet:Boolean = false,
    val lowerCaseAlphabet:Boolean = false,
    val number:Boolean = false,
    val specialCharacter:Boolean = false,
)
