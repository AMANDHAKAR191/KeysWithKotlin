package com.aman.keyswithkotlin.chats.presentation

import com.aman.keyswithkotlin.chats.domain.model.ChatModelClass

data class ChatMessagesState (
    var chatMessagesList:List<ChatModelClass>? = null,
    var chatMessage:String = "",
    val error:String?  = "",
    val isLoading:Boolean = false
)