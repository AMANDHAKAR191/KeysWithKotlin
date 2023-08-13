package com.aman.keyswithkotlin.chats.domain.model

data class MessageUserList(
    var publicUid: String? = null,
    var publicUname: String? = null,
    var profileAccess: Boolean = false,
    var profileUrl:String? = "",
    var UserPersonalChatList: Map<String, UserPersonalChatList>? = null
)
