package com.aman.keyswithkotlin.passwords.domain.model

data class Password constructor(
    val userName: String = "",
    val password: String = "",
    val websiteName: String = "",
    val websiteLink: String = ""
) {

}

class InvalidPasswordException(message: String) : Exception(message)
