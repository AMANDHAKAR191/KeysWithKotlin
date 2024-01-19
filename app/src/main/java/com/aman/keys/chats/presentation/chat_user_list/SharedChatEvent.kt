package com.aman.keys.chats.presentation.chat_user_list

import com.aman.keys.chats.domain.model.UserPersonalChatList
import com.aman.keys.notes.domain.model.Note
import com.aman.keys.passwords.domain.model.Password

sealed class SharedChatEvent {
    data class OpenSharedChat(val person: UserPersonalChatList, val chatRoomId:String) : SharedChatEvent()
    data class SharePasswordItem(val passwordItem:Password?): SharedChatEvent()
    data class ShareNoteItem(val noteItem:Note?): SharedChatEvent()
}