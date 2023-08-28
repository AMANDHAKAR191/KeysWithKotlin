package com.aman.keyswithkotlin.chats.presentation

import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList
import com.aman.keyswithkotlin.notes.domain.model.Note
import com.aman.keyswithkotlin.passwords.domain.model.Password

sealed class SharedChatEvent {
    data class OpenSharedChat(val person: UserPersonalChatList, val chatRoomId:String) : SharedChatEvent()
    data class SharePasswordItem(val passwordItem:Password?):SharedChatEvent()
    data class ShareNoteItem(val noteItem:Note?):SharedChatEvent()
}