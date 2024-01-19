package com.aman.keys.core.util

sealed class Response<out T> {
    data class Loading(val message:String?) : Response<Nothing>()

    data class Success<out T, out U>(
        val data: T? = null,
        val status: U? = null
    ) : Response<Pair<T?, U?>>()

    data class Failure(val e: Throwable) : Response<Nothing>()
}
