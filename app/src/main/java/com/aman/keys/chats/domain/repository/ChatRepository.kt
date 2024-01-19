package com.aman.keys.chats.domain.repository

import com.aman.keys.chats.domain.model.ChatModelClass
import com.aman.keys.chats.domain.model.MessageUserList
import com.aman.keys.chats.domain.model.UserPersonalChatList
import com.aman.keys.core.util.Response
import kotlinx.coroutines.flow.Flow
typealias SendMessageResponse = Response<Pair<String?, Boolean?>>
typealias CreateChatUserResponse = Response<Pair<String?, Boolean?>>
typealias GetChatUsersResponse = Response<Pair<MutableList<UserPersonalChatList>?, Boolean?>>
typealias GetUserChatMessagesResponse = Response<Pair<MutableList<ChatModelClass>?, Boolean?>>
typealias GetChatProfileDataResponse = Response<Pair<MessageUserList?, Boolean?>>

interface ChatRepository {
    fun sendMessage(
        chatRoomId: String,
        chat: ChatModelClass
    ): Flow<SendMessageResponse>

    fun createUserInSenderChat(
        otherUserPublicUid: String,
        userPersonalChatList: UserPersonalChatList
    ): Flow<CreateChatUserResponse>

    fun createUserInReceiverChat(
        otherUserPublicUid: String,
        userPersonalChatList: UserPersonalChatList
    ): Flow<CreateChatUserResponse>
    fun getChatUsers(): Flow<GetChatUsersResponse>

    fun getChatProfileDataByPublicUID(otherUserPublicUid: String): Flow<GetChatProfileDataResponse>
    fun getUserChatMessages(chatRoomId: String): Flow<GetUserChatMessagesResponse>
}