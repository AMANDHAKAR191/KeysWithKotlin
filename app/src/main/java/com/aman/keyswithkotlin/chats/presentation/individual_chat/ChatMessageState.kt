package com.aman.keyswithkotlin.chats.presentation.individual_chat

import com.aman.keyswithkotlin.chats.domain.model.ChatModelClass

data class ChatMessageState (
    var chatMessagesList:List<ChatModelClass>? = null,
    var chatMessage:String = "",
    val error:String?  = "",
    val isLoading:Boolean = false
)