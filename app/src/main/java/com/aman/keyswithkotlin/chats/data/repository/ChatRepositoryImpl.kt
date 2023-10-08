package com.aman.keyswithkotlin.chats.data.repository

import com.aman.keyswithkotlin.chats.domain.model.ChatModelClass
import com.aman.keyswithkotlin.chats.domain.model.MessageUserList
import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList
import com.aman.keyswithkotlin.chats.domain.repository.ChatRepository
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.core.util.TimeStampUtil
import com.aman.keyswithkotlin.di.AESKeySpecs
import com.aman.keyswithkotlin.di.PublicUID
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
    @PublicUID
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
                        trySend(Response.Success("sent"))
                    }
                    .addOnFailureListener {
                        trySend(Response.Failure(it))
                    }
            }
            awaitClose {
                close()
            }
        }

    override fun createUserInSenderChat(
        otherUserPublicUid: String,
        userPersonalChatList: UserPersonalChatList
    ): Flow<Response<Pair<String?, Boolean?>>> = callbackFlow {
        trySend(Response.Loading)
        database.reference.child("messageUserList").child(publicUID)
            .child("UserPersonalChatList").child(otherUserPublicUid)
            .setValue(userPersonalChatList).await()
        trySend(Response.Success(data = "Chat User Created"))
        awaitClose { close() }
    }
    override fun createUserInReceiverChat(
        otherUserPublicUid: String,
        userPersonalChatList: UserPersonalChatList
    ): Flow<Response<Pair<String?, Boolean?>>> = callbackFlow {
        trySend(Response.Loading)
        database.reference.child("messageUserList").child(otherUserPublicUid)
            .child("UserPersonalChatList").child(publicUID)
            .setValue(userPersonalChatList).await()
        trySend(Response.Success(data = "Chat User Created"))
        awaitClose { close() }
    }

    override fun getChatProfileDataByPublicUID(otherUserPublicUid: String): Flow<Response<Pair<MessageUserList?, Boolean?>>> =
        callbackFlow {
            val reference = database.reference.child("messageUserList").orderByChild("publicUid")
                .equalTo(otherUserPublicUid)
            trySend(Response.Loading)
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        println("dataSnapshot: $dataSnapshot")
                        for (ds in dataSnapshot.children) {
                            println("ds: $ds")
                            val item = ds.getValue(MessageUserList::class.java)
                            if (item != null) {
                                item.UserPersonalChatList?.let {
                                    println("Map: $it")
                                    it.keys.forEach { key ->
                                        println("key: $key")
                                        println("value: ${it[key]}")
                                        println("otherUserPublicUid: ${it[key]?.otherUserPublicUid}")
                                        if (it[key]?.otherUserPublicUid.equals(publicUID)) {
                                            println("Check: Matched")
                                        }
                                    }
                                    println("publicUID: $publicUID")
                                    println("commonChatRoomId: ${it["amandhaker191"]}")
                                }
                                trySend(Response.Success(data = item))
                            }
                        }
                    } else {
                        trySend(Response.Failure(Throwable("User Not Found")))
                    }

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

    override fun getChatUsers(): Flow<Response<Pair<MutableList<UserPersonalChatList>?, Boolean?>>> =
        callbackFlow {
            trySend(Response.Loading)
            val reference = database.reference.child("messageUserList").child(publicUID)
                .child("UserPersonalChatList")
            reference.keepSynced(true)
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        _chatUsersList.clear()
                        for (ds in dataSnapshot.children) {
                            val items = ds.getValue(UserPersonalChatList::class.java)
                            if (items != null) {
                                _chatUsersList.add(items)
                            }
                        }
                        trySend(Response.Success(data = _chatUsersList))
                    } else {
                        trySend(Response.Failure(Throwable("No user")))
                    }

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
                    if (dataSnapshot.exists()) {
                        _chatMessagesList.clear()
                        for (ds in dataSnapshot.children) {
                            val items = ds.getValue(ChatModelClass::class.java)
                            if (items != null) {
                                _chatMessagesList.add(items)
                            }

                        }
                        trySend(Response.Success(data = _chatMessagesList))
                    }
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