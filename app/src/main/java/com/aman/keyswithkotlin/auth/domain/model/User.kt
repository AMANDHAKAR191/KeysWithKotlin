package com.aman.keyswithkotlin.auth.domain.model

data class User(
    val displayName:String?,
    val email:String?,
    val publicUID:String?,
    val photoUrl:String?,
    val createdAt:String?
)
