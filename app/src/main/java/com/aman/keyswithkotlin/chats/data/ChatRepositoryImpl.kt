package com.aman.keyswithkotlin.chats.data

import com.aman.keyswithkotlin.chats.domain.model.Chat
import com.aman.keyswithkotlin.chats.domain.repository.ChatRepository
import com.aman.keyswithkotlin.core.util.Response
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ChatRepositoryImpl(
    private val database: FirebaseDatabase,
    private val UID: String
) : ChatRepository {
    override fun sendMessage(chat: Chat): Flow<Response<Pair<MutableList<String>?, Boolean?>>> =
        callbackFlow {
//        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
//        val current = LocalDateTime.now()
//        val formatted = current.format(formatter)
//
//        val reference = database.reference.child("Passwords").child(UID)
//        trySend(Response.Loading)
//        val _chat = Chat(
//
//        )
//
//        try {
//            println("password.timestamp :${password.timestamp}")
//            reference.child(password.websiteName).child(_password.timestamp)
//                .setValue(_password)
//                .addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        trySend(Response.Success("Password is successfully saved"))
//                    }
//                }
//                .addOnFailureListener {
//                    trySend(Response.Failure(it))
//                }
//            awaitClose {
//                close()
//            }
//        } catch (e: Exception) {
//            Response.Failure(e)
//        }
        }
}