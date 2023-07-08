package com.aman.keyswithkotlin.core.util

sealed class Response<out T> {
    object Loading : Response<Nothing>()

    data class Success<out T, out U>(
        val data: T? = null,
        val status: U? = null
    ) : Response<Pair<T?, U?>>()

    data class Failure(val e: Throwable) : Response<Nothing>()
}
