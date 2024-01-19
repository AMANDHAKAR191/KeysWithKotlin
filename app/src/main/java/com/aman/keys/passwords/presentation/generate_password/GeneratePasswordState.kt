package com.aman.keys.passwords.presentation.generate_password

import com.aman.keys.passwords.domain.model.GeneratedPasswordModelClass

data class GeneratePasswordState(
    val generatedPassword:String = "",
    val slider:Int = 4,
    val upperCaseAlphabet:Boolean = false,
    val lowerCaseAlphabet:Boolean = false,
    val number:Boolean = false,
    val specialCharacter:Boolean = false,
    val recentGeneratedPasswordList:MutableList<GeneratedPasswordModelClass> = mutableListOf()
)
