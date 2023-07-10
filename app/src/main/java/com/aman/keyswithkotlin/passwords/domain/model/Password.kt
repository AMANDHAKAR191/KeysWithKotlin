package com.aman.keyswithkotlin.passwords.domain.model

data class Password constructor(
    var userName: String = "",
    var password: String = "",
    val websiteName: String = "",
    val websiteLink: String = "",
    val timestamp: String = "",
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombination = listOf(
            "$websiteName",
            "$userName",
            "${websiteName.first()}",
            "${userName.first()}"
        )
        return matchingCombination.any {
            it.contains(query, ignoreCase = true)
        }
    }
}

class InvalidPasswordException(message: String) : Exception(message)
