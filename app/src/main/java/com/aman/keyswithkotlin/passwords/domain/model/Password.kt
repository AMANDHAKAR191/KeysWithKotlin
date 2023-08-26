package com.aman.keyswithkotlin.passwords.domain.model

import android.os.Parcel
import android.os.Parcelable

data class Password constructor(
    var userName: String = "",
    var password: String = "",
    val websiteName: String = "",
    val websiteLink: String = "",
    val timestamp: String = "",
    val lastUsedTimeStamp: String = ""
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userName)
        parcel.writeString(password)
        parcel.writeString(websiteName)
        parcel.writeString(websiteLink)
        parcel.writeString(timestamp)
        parcel.writeString(lastUsedTimeStamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Password> {
        override fun createFromParcel(parcel: Parcel): Password {
            return Password(parcel)
        }

        override fun newArray(size: Int): Array<Password?> {
            return arrayOfNulls(size)
        }
    }
}

class InvalidPasswordException(message: String) : Exception(message)
