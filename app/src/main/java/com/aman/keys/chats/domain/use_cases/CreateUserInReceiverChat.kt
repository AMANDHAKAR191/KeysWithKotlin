package com.aman.keys.chats.domain.use_cases

import com.aman.keys.chats.domain.model.UserPersonalChatList
import com.aman.keys.chats.domain.repository.ChatRepository
import com.aman.keys.chats.domain.repository.CreateChatUserResponse
import kotlinx.coroutines.flow.Flow

class CreateUserInReceiverChat(
    private val repository: ChatRepository
) {
    operator fun invoke(
        otherUserPublicUid: String,
        userPersonalChatList: UserPersonalChatList
    ): Flow<CreateChatUserResponse> {
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