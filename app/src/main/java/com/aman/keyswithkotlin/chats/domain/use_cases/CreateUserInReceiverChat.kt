package com.aman.keyswithkotlin.chats.domain.use_cases

import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList
import com.aman.keyswithkotlin.chats.domain.repository.ChatRepository
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow

class CreateUserInReceiverChat(
    private val repository: ChatRepository
) {
    operator fun invoke(
        otherUserPublicUid: String,
        userPersonalChatList: UserPersonalChatList
    ): Flow<Response<Pair<String?, Boolean?>>> {
        val _userPersonalChatList =
                UserPersonalChatList(
                    otherUserPublicUid = userPersonalChatList.otherUserPublicUid,
                    otherUserPublicUname = userPersonalChatList.otherUserPublicUname,
                    otherUserProfileUrl = userPersonalChatList.otherUserProfileUrl,
                    commonChatRoomId = userPersonalChatList.commonChatRoomId,
                    commonEncryptionKey = userPersonalChatList.commonEncryptionKey,
                    commonEncryptionIv = userPersonalChatList.commonEncryptionIv
                )

        return repository.createUserInReceiverChat(otherUserPublicUid, _userPersonalChatList)
    }
}