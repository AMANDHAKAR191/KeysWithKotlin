package com.aman.keyswithkotlin.auth.domain.model

import com.aman.keyswithkotlin.core.AES

data class User(
    var displayName: String? = "",
    var email: String? = "",
    var photoUrl: String? = "",
    var aesKey: String? = "",
    var aesIV: String? = "",
    var privateUID: String? = "",
    var publicUID: String? = "",
    var createdAt: String? = ""
)
