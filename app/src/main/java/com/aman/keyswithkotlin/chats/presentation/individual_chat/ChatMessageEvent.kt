package com.aman.keyswithkotlin.chats.presentation.individual_chat

sealed class ChatMessageEvent {
    data class OnMessageEntered(val value:String): ChatMessageEvent()
    object SendMessage: ChatMessageEvent()
}