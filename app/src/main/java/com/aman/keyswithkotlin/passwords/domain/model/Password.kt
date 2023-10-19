package com.aman.keyswithkotlin.passwords.domain.model

import android.os.Parcel
import android.os.Parcelable

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
