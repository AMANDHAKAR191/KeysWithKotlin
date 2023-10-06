package com.aman.keyswithkotlin.chats.domain.model

import com.aman.keyswithkotlin.auth.data.repository.AuthRepositoryImpl
import com.aman.keyswithkotlin.core.GeneratorClass


data class Chat(
//    val messageId:Int = GeneratorClass().randomIdGenerator(5),
    val message: String,
    val time: String,
    val userType: String = UserType.SENDER.toString(),
)

enum class UserType {
    SENDER, RECEIVER
}

val chatList = listOf(
    Chat(
        "Hey! How have you been?",
        "12:15 PM",
        UserType.RECEIVER.toString()
    ),
    Chat(
        "Wanna catch up for a beer?",
        "12:17 PM",
        UserType.RECEIVER.toString()
    ),
    Chat(
        "Awesome! Let’s meet up",
        "12:19 PM",
        UserType.SENDER.toString()
    ),
    Chat(
        "Can I also get my cousin along? Will that be okay?",
        "12:20 PM",
        UserType.SENDER.toString()
    ),
    Chat(
        "Yeah sure! get him too.",
        "12:21 PM",
        UserType.RECEIVER.toString()
    ),
    Chat(
        "Hey! How have you been?",
        "12:15 PM",
        UserType.RECEIVER.toString()
    ),
    Chat(
        "Wanna catch up for a beer?",
        "12:17 PM",
        UserType.RECEIVER.toString()
    ),
    Chat(
        "Awesome! Let’s meet up",
        "12:19 PM",
        UserType.SENDER.toString()
    ),
    Chat(
        "Can I also get my cousin along? Will that be okay?",
        "12:20 PM",
        UserType.SENDER.toString()
    ),
    Chat(
        "Yeah sure! get him too.",
        "12:21 PM",
        UserType.RECEIVER.toString()
    ),
    Chat(
        "Hey! How have you been?",
        "12:15 PM",
        UserType.RECEIVER.toString()
    ),
    Chat(
        "Wanna catch up for a beer?",
        "12:17 PM",
        UserType.RECEIVER.toString()
    ),
    Chat(
        "Awesome! Let’s meet up",
        "12:19 PM",
        UserType.SENDER.toString()
    ),
    Chat(
        "Can I also get my cousin along? Will that be okay?",
        "12:20 PM",
        UserType.SENDER.toString()
    ),
    Chat(
        "Yeah sure! get him too.",
        "12:21 PM",
        UserType.RECEIVER.toString()
    )
)