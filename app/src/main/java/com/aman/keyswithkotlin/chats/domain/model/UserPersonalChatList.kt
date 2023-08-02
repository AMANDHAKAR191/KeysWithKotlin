package com.aman.keyswithkotlin.chats.domain.model

class UserPersonalChatList {
    var otherUserPublicUid: String? = ""
    var otherUserPublicUname: String? = ""
    var otherUserProfileUrl:String? = ""
    var commonEncryptionKey: String? = ""
    var commonEncryptionIv: String? = ""
    var isKnowUser = false
    var lastMessage: String? = ""
}