package com.aman.keys.chats.domain.model

import com.aman.keys.notes.domain.model.Note
import com.aman.keys.passwords.domain.model.Password

data class ChatModelClass(
    var message: String? = null,
    var dateAndTime: String? = null,
    var publicUid: String? = null,
    var type: String? = UserType.SENDER.toString(),
    var status: String? = null,
    var passwordModelClass: Password? = null,
    var noteModelClass: Note? = null
)