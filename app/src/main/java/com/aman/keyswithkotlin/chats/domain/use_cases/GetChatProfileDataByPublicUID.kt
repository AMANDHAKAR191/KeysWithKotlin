package com.aman.keyswithkotlin.chats.domain.use_cases

import com.aman.keyswithkotlin.chats.domain.model.MessageUserList
import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList
import com.aman.keyswithkotlin.chats.domain.repository.ChatRepository
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow

class GetChatProfileDataByPublicUID (
    private val repository: ChatRepository
) {
    operator fun invoke(otherUserPublicUid:String): Flow<Response<Pair<MessageUserList?, Boolean?>>> {
        return repository.getChatProfileDataByPublicUID(otherUserPublicUid)
    }
}