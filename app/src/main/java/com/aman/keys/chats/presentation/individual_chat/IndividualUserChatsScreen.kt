package com.aman.keys.chats.presentation.individual_chat

import UIEvents
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aman.keys.chats.domain.model.ChatModelClass
import com.aman.keys.chats.domain.model.UserPersonalChatList
import com.aman.keys.chats.presentation.chat_user_list.SharedChatEvent
import com.aman.keys.chats.presentation.chat_user_list.SharedChatState
import com.aman.keys.chats.presentation.chat_user_list.SpacerHeight
import com.aman.keys.chats.presentation.chat_user_list.SpacerWidth
import com.aman.keys.core.util.TimeStampUtil
import com.aman.keys.passwords.domain.model.Password
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun IndividualChatScreen(
    _state: StateFlow<ChatMessageState>,
    _sharedChatState: StateFlow<SharedChatState>,
    eventFlowState: SharedFlow<UIEvents>,
    onChatEvent: (ChatMessageEvent) -> Unit,
    onSharedChatEvent: (SharedChatEvent) -> Unit,
    navigateBack: () -> Unit,
    sendNotification: (String, String, String) -> Unit
) {
    val state = _state.collectAsState()
    val sharedChatState = _sharedChatState.collectAsState()
    val data = sharedChatState.value.person
    val chatMessages: List<ChatModelClass>? = state.value.chatMessagesList
    val messageTextValue = state.value.chatMessage
    val lazyColumnState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    var isItemShared by remember { mutableStateOf(false) }
    var passwordItemToShare: Password? = null

    //UIEvents
    LaunchedEffect(key1 = true) {
        eventFlowState.collectLatest { event ->
            when (event) {
                is UIEvents.SendNotification -> {
                    println("otherUserPublicUid: ${data?.otherUserPublicUid} my public Id: ${event.publicUid}")
                    sendNotification(
                        data?.otherUserPublicUid!!,
                        event.publicUid,
                        event.messageBody ?: "test message"
                    )
                }

                else -> {}
            }
        }

    }

    //show dialog when item is shared
    LaunchedEffect(key1 = sharedChatState.value.sharedPasswordItem) {
        if (sharedChatState.value.sharedPasswordItem != null) {
            isItemShared = true
        }

    }
    LaunchedEffect(key1 = sharedChatState.value.sharedNoteItem) {
        if (sharedChatState.value.sharedNoteItem != null) {
            isItemShared = true
        }
    }

    // Scroll to the bottom of the LazyColumn when a new item is added
    LaunchedEffect(
        key1 = state.value.chatMessagesList?.size,
        key2 = state.value.isMessageReceived
    ) {
        if (chatMessages.isNullOrEmpty()) {
            //todo do nothing
        } else {
            lazyColumnState.animateScrollToItem(lazyColumnState.layoutInfo.totalItemsCount)
        }
    }

    if (isItemShared) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                isItemShared = false
                onSharedChatEvent(SharedChatEvent.SharePasswordItem(null))
                onSharedChatEvent(SharedChatEvent.ShareNoteItem(null))
            },
            title = {
                Text(text = "Share Item", color = MaterialTheme.colorScheme.error)
            },
            text = {
                Row {
                    AsyncImage(
                        modifier = Modifier
                            .clip(
                                CircleShape
                            )
                            .background(Color.Yellow)
                            .padding(2.dp)
                            .clip(CircleShape),
                        model = data?.otherUserProfileUrl,
                        contentDescription = "Profile image"
                    )
                    SpacerWidth()
                    Text(
                        text = "${data?.otherUserPublicUname}\nAre you sure, you want share password?",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    passwordItemToShare = sharedChatState.value.sharedPasswordItem
                    isItemShared = false
                    sendMessage(
                        chatMessages,
                        onChatEvent,
                        data,
                        sharedChatState,
                        onSharedChatEvent
                    )
                }) {
                    Text("Share")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isItemShared = false
                        onSharedChatEvent(SharedChatEvent.SharePasswordItem(null))
                        onSharedChatEvent(SharedChatEvent.ShareNoteItem(null))
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    UserNameRow(
                        person = data,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(
                                top = 20.dp,
//                                start = 20.dp,
                                end = 20.dp,
                                bottom = 20.dp
                            )
                    )
                },
                navigationIcon = {
                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurface,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        onClick = { navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        content = { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                        )
                        .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(top = 30.dp, start = 20.dp, end = 20.dp),
                        state = lazyColumnState
                    ) {
                        if (chatMessages != null) {
                            val timeStampUtil = TimeStampUtil()
                            items(chatMessages) {
                                ChatRow(
                                    chat = it,
                                    timeStampUtil = timeStampUtil,
                                    senderPublicUID = state.value.senderPublicUID!!
                                )
                            }
                        } else {
                        }
                    }
                    CustomTextField(
                        text = messageTextValue, onValueChange = {
                            onChatEvent(ChatMessageEvent.OnMessageEntered(it))
                        },
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 5.dp),
                        onTrailingIconButtonClicked = {
                            sendMessage(
                                chatMessages,
                                onChatEvent,
                                data,
                                sharedChatState,
                                onSharedChatEvent
                            )
                        }
                    )
                }
            }
        }
    )

}

private fun sendMessage(
    chatMessages: List<ChatModelClass>?,
    onChatEvent: (ChatMessageEvent) -> Unit,
    data: UserPersonalChatList?,
    sharedChatState: State<SharedChatState>,
    onSharedChatEvent: (SharedChatEvent) -> Unit
) {
    if (chatMessages.isNullOrEmpty()) {
        onChatEvent(
            ChatMessageEvent.SendMessage(
                data?.commonChatRoomId!!,
                person = data,
                sharedChatState.value.sharedPasswordItem
            )
        )
    } else {
        onChatEvent(
            ChatMessageEvent.SendMessage(
                data?.commonChatRoomId!!,
                person = null,
                sharedChatState.value.sharedPasswordItem
            )
        )
    }
    onSharedChatEvent(SharedChatEvent.SharePasswordItem(null))
    onSharedChatEvent(SharedChatEvent.ShareNoteItem(null))
}

@Composable
fun ChatRow(
    senderPublicUID: String,
    chat: ChatModelClass,
    timeStampUtil: TimeStampUtil
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (chat.publicUid != senderPublicUID) Alignment.Start else Alignment.End
    ) {
        Column(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    RoundedCornerShape(10.dp)
                ),
            verticalArrangement = Arrangement.Center
        ) {
            chat.passwordModelClass?.let {
                Text(
                    text = it.websiteName, style = TextStyle(
                        color = Color.Black,
                        fontSize = 15.sp
                    ),
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 15.dp),
                    textAlign = TextAlign.End
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(0.7f),
            horizontalAlignment = if (chat.publicUid != senderPublicUID) Alignment.Start else Alignment.End,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = chat.message ?: "",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp
                ),
            )
            Text(
                text = timeStampUtil.getTime(chat.dateAndTime!!),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                ),
            )
        }
        SpacerHeight(height = 20.dp)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    text: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onTrailingIconButtonClicked: () -> Unit
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TextField(
            value = text, onValueChange = { onValueChange(it) },
            placeholder = {
                Text(
                    text = "Type",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = TextAlign.Center
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Gray,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Default
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (text.isNotEmpty()) {
                        onTrailingIconButtonClicked()
                    }
                }
            ),
            modifier = Modifier
                .padding(all = 5.dp)
                .weight(1f),
            maxLines = 5,
            shape = RoundedCornerShape(20.dp)
        )
        IconButton(
            onClick = {
                onTrailingIconButtonClicked()
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                if (text != "") Icons.Default.Send else Icons.Default.Add,
                contentDescription = "",
                modifier = Modifier.size(32.dp)
            )
        }
    }

}


@Composable
fun UserNameRow(
    modifier: Modifier = Modifier,
    person: UserPersonalChatList? = null
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        person?.let { personData ->
            personData.otherUserProfileUrl?.let {
                AsyncImage(
                    model = personData.otherUserProfileUrl,
                    contentDescription = "",
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(
                            CircleShape
                        )
                        .background(Color.Yellow)
                        .padding(2.dp)
                        .clip(CircleShape)
                )
            }
            SpacerWidth()
            Column {
                Text(
                    text = personData.otherUserPublicUname ?: "Username", style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = "Online", style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}


//@Preview
//@Composable
//fun IndividualChatScreenPreivew() {
//    IndividualChatScreen(
//        data = Person(),
//        onSharedChatEvent = {},
//        navigateToPasswordScreen = {}
//    )
//}