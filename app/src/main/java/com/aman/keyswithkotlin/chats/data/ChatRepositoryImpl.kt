package com.aman.keyswithkotlin.chats.data

import com.aman.keyswithkotlin.chats.domain.model.ChatModelClass
import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList
import com.aman.keyswithkotlin.chats.domain.repository.ChatRepository
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.core.util.TimeStampUtil
import com.aman.keyswithkotlin.di.AESKeySpecs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepositoryImpl(
    private val database: FirebaseDatabase,
    private val UID: String,
    private val publicUID: String,
    private val aesKeySpecs: AESKeySpecs
) : ChatRepository {

    private val _chatUsersList = mutableListOf<UserPersonalChatList>()
    private val _chatMessagesList = mutableListOf<ChatModelClass>()
    override fun sendMessage(
        chatRoomId: String,
        chat: ChatModelClass
    ): Flow<Response<Pair<String?, Boolean?>>> =
        callbackFlow {
            val timeStampUtil = TimeStampUtil()

            val reference = database.reference.child("messages").child(chatRoomId)
            trySend(Response.Success("Sending..."))

            val _message = ChatModelClass(
                message = chat.message,
                dateAndTime = timeStampUtil.generateTimestamp(),
                publicUid = chat.publicUid,
                type = chat.type,
                status = chat.status,
                passwordModelClass = chat.passwordModelClass,
                noteModelClass = chat.noteModelClass
            )

            _message.dateAndTime?.let { timeStamp ->
                reference.child((timeStamp)).setValue(_message)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            trySend(Response.Success("sent"))
                        } else {
                            trySend(Response.Success("Failed!"))
                        }
                    }
                    .addOnFailureListener {
                        trySend(Response.Failure(it))
                    }
            }
            awaitClose {
                close()
            }
        }

    override fun createChat(
        otherUserPublicUid: String,
        userPersonalChatList: UserPersonalChatList
    ): Flow<Response<Pair<String?, Boolean?>>> = callbackFlow{
        database.reference.child("messageUserList").child(publicUID)
            .child("UserPersonalChatList").child(otherUserPublicUid)
            .setValue(userPersonalChatList).await()

        awaitClose{close()}
    }

    override fun getChatUsers(): Flow<Response<Pair<MutableList<UserPersonalChatList>?, Boolean?>>> =
        callbackFlow {
            val reference = database.reference.child("messageUserList").child(publicUID)
                .child("userPersonalChatList")
            reference.keepSynced(true)
            trySend(Response.Loading)
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    _chatUsersList.clear()
                    for (ds in dataSnapshot.children) {
                        val items = ds.getValue(UserPersonalChatList::class.java)
                        if (items != null) {
                            println("items: $items")
                            _chatUsersList.add(items)
                        }
                    }
                    trySend(Response.Success(data = _chatUsersList))

                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Response.Failure(error.toException()))
                }
            }

            reference.addValueEventListener(listener)
            awaitClose {
                reference.removeEventListener(listener)
                close()
            }
        }

    override fun getUserChatMessages(chatRoomId: String): Flow<Response<Pair<MutableList<ChatModelClass>?, Boolean?>>> =
        callbackFlow {
            val reference = database.reference.child("messages").child(chatRoomId)
            reference.keepSynced(true)
            trySend(Response.Loading)
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    _chatMessagesList.clear()
                    println("dataSnapshot: $dataSnapshot")
                    for (ds in dataSnapshot.children) {
                        val items = ds.getValue(ChatModelClass::class.java)
                        if (items != null) {
                            println("items: $items")
                            _chatMessagesList.add(items)
                        }
                    }
                    println("passwords: $_chatMessagesList")
                    trySend(Response.Success(data = _chatMessagesList))

                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Response.Failure(error.toException()))
                }
            }

            reference.addValueEventListener(listener)
            awaitClose {
                reference.removeEventListener(listener)
                close()
            }
        }
}