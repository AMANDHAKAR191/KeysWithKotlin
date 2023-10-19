package com.aman.keyswithkotlin.chats.presentation

sealed class ChatUserEvent {
    data class CreateChatUser(val value:String) : ChatUserEvent()

    object DisableTutorial:ChatUserEvent()
}
