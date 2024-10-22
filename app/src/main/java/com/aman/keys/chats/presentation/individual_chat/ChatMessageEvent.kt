package com.aman.keys.chats.presentation.individual_chat

import com.aman.keys.chats.domain.model.UserPersonalChatList
import com.aman.keys.passwords.domain.model.Password

sealed class ChatMessageEvent {

    data class OnMessageEntered(val value:String): ChatMessageEvent()
    data class SendMessage(val commonChatRoomId:String, val person:UserPersonalChatList? = null, val passwordItemValue:Password?): ChatMessageEvent()
}