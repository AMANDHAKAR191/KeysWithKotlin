package com.aman.keys.passwords.domain.model

data class Password constructor(
    var userName: String = "",
    var password: String = "",
    var websiteName: String = "",
    var linkTo: List<String> = emptyList(),
    var timestamp: String = "",
    var lastUsedTimeStamp: String = "",
//    var encryptionIV:String = ""
){
    fun doesMatchSearchQuery(query: String): Boolean {
        // if username is empty is then don't match with username
        val matchingCombination = if (userName != "") listOf(
            "$websiteName",
            "$userName",
            "${websiteName.first()}",
            "${userName.first()}"
        ) else listOf(
            "$websiteName",
            "${websiteName.first()}",
        )
        return matchingCombination.any {
            it.contains(query, ignoreCase = true)
        }
    }

}

class InvalidPasswordException(message: String) : Exception(message)
