package com.aman.keys.chats.presentation.chat_user_list

sealed class ChatUserEvent {
    data class CreateChatUser(val value:String) : ChatUserEvent()

    object DisableTutorial: ChatUserEvent()
}
