package com.aman.keys.chats.domain.model

 data class UserPersonalChatList (
    var otherUserPublicUid: String? = "",
    var otherUserPublicUname: String? = "",
    var otherUserProfileUrl:String? = "",
    val commonChatRoomId:String? = "",
    var commonEncryptionKey: String? = "",
    var commonEncryptionIv: String? = "",
    var isKnowUser:Boolean = false,
    var lastMessage: String? = ""
)