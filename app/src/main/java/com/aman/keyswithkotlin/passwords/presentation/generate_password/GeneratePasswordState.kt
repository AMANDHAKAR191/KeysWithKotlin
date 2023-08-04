package com.aman.keyswithkotlin.passwords.presentation.generate_password

import com.aman.keyswithkotlin.passwords.domain.model.GeneratedPasswordModelClass
import com.aman.keyswithkotlin.passwords.domain.use_cases.GetRecentGeneratedPasswords

data class GeneratePasswordState(
    val generatedPassword:String = "",
    val slider:Int = 4,
    val upperCaseAlphabet:Boolean = false,
    val lowerCaseAlphabet:Boolean = false,
    val number:Boolean = false,
    val specialCharacter:Boolean = false,
    val recentGeneratedPasswordList:MutableList<GeneratedPasswordModelClass> = mutableListOf()
)
