package com.aman.keyswithkotlin.chats.domain.use_cases

import com.aman.keyswithkotlin.chats.domain.model.ChatModelClass
import com.aman.keyswithkotlin.chats.domain.repository.ChatRepository
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow

class GetUserChatMessages(
    private val repository: ChatRepository
) {
    operator fun invoke(chatRoomId:String): Flow<Response<Pair<MutableList<ChatModelClass>?, Boolean?>>> {
        return repository.getUserChatMessages(chatRoomId)
    }
}