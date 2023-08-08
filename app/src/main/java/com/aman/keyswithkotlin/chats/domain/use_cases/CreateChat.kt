package com.aman.keyswithkotlin.chats.domain.use_cases

import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList
import com.aman.keyswithkotlin.chats.domain.repository.ChatRepository
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow

class CreateChat(
    private val repository: ChatRepository
) {
    operator fun invoke(
        otherUserPublicUid: String,
        userPersonalChatList: UserPersonalChatList
    ): Flow<Response<Pair<String?, Boolean?>>> {
        return repository.createChat(otherUserPublicUid, userPersonalChatList)
    }
}