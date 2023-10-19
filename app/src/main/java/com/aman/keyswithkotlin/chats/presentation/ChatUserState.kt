package com.aman.keyswithkotlin.chats.presentation

import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList
import com.aman.keyswithkotlin.core.util.TutorialType

data class ChatUserState (
    var username:String = "",
    var chatUsersList:List<UserPersonalChatList>? = emptyList(),
    val error:String?  = "",
    val isLoading:Boolean = false,
)