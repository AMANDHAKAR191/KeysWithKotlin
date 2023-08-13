package com.aman.keyswithkotlin.chats.domain.repository

import com.aman.keyswithkotlin.chats.domain.model.ChatModelClass
import com.aman.keyswithkotlin.chats.domain.model.MessageUserList
import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun sendMessage(
        chatRoomId: String,
        chat: ChatModelClass
    ): Flow<Response<Pair<String?, Boolean?>>>

    fun createChatUser(
        otherUserPublicUid: String,
        userPersonalChatList: UserPersonalChatList
    ): Flow<Response<Pair<String?, Boolean?>>>

    fun getChatUsers(): Flow<Response<Pair<MutableList<UserPersonalChatList>?, Boolean?>>>

    fun getChatProfileDataByPublicUID(otherUserPublicUid: String): Flow<Response<Pair<MessageUserList?, Boolean?>>>
    fun getUserChatMessages(chatRoomId: String): Flow<Response<Pair<MutableList<ChatModelClass>?, Boolean?>>>
}