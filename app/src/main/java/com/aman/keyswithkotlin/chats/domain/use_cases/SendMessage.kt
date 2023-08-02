package com.aman.keyswithkotlin.chats.domain.use_cases

import com.aman.keyswithkotlin.chats.domain.model.ChatModelClass
import com.aman.keyswithkotlin.chats.domain.repository.ChatRepository
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow

class SendMessage(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(chatRoomId:String, chat: ChatModelClass): Flow<Response<Pair<String?, Boolean?>>> {
        return chatRepository.sendMessage(chatRoomId, chat)
    }
}