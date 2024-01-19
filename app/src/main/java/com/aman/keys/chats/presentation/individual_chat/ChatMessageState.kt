package com.aman.keys.chats.presentation.individual_chat

import com.aman.keys.chats.domain.model.ChatModelClass

data class ChatMessageState (
    var senderPublicUID:String? = "",
    var chatMessagesList:List<ChatModelClass>? = null,
    var isMessageReceived:Int = 0,
    var chatMessage:String = "",
    val error:String?  = "",
    val isLoading:Boolean = false,
    var commonChatRoomId:String? = ""
)