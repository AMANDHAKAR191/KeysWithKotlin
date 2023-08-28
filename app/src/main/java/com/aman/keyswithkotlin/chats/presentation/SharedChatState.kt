package com.aman.keyswithkotlin.chats.presentation

import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList
import com.aman.keyswithkotlin.notes.domain.model.Note
import com.aman.keyswithkotlin.passwords.domain.model.Password

data class SharedChatState(
    var person: UserPersonalChatList? = null,
    var sharedPasswordItem:Password? = null,
    var sharedNoteItem:Note? = null
)