package com.aman.keys.chats.presentation.chat_user_list

import com.aman.keys.chats.domain.model.UserPersonalChatList
import com.aman.keys.notes.domain.model.Note
import com.aman.keys.passwords.domain.model.Password

data class SharedChatState(
    var person: UserPersonalChatList? = null,
    var sharedPasswordItem:Password? = null,
    var sharedNoteItem:Note? = null
)