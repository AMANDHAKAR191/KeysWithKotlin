package com.aman.keyswithkotlin.chats.presentation

import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList

data class ChatUserState (
    var chatUsersList:List<UserPersonalChatList>? = null,
    val error:String?  = "",
    val isLoading:Boolean = false
)