package com.aman.keyswithkotlin.chats.presentation.individual_chat

import com.aman.keyswithkotlin.passwords.domain.model.Password

sealed class ChatMessageEvent {

    data class OnMessageEntered(val value:String): ChatMessageEvent()
    data class SendMessage(val commonChatRoomId:String, val passwordItemValue:Password?): ChatMessageEvent()
}