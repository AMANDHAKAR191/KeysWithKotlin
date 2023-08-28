package com.aman.keyswithkotlin.chats.presentation.individual_chat

import UIEvents
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.aman.keyswithkotlin.chats.domain.model.ChatModelClass
import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList
import com.aman.keyswithkotlin.chats.presentation.SharedChatEvent
import com.aman.keyswithkotlin.chats.presentation.SharedChatState
import com.aman.keyswithkotlin.chats.presentation.SpacerWidth
import com.aman.keyswithkotlin.core.util.TimeStampUtil
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.ui.theme.Pink80
import com.aman.keyswithkotlin.ui.theme.RedOrange
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualChatScreen(
    _state: StateFlow<ChatMessageState>,
    _sharedChatState: StateFlow<SharedChatState>,
    eventFlowState: SharedFlow<UIEvents>,
    onChatEvent: (ChatMessageEvent) -> Unit,
    onSharedChatEvent: (SharedChatEvent) -> Unit,
    navigateToPasswordScreen: () -> Unit
) {
    val state = _state.collectAsState()
    val sharedChatState = _sharedChatState.collectAsState()
    val data = sharedChatState.value.person
    val chatMessages: List<ChatModelClass>? = state.value.chatMessagesList
    val messageTextValue = state.value.chatMessage
    val lazyColumnState = rememberLazyListState()

    var isItemShared by remember { mutableStateOf(false) }
    var passwordItemToShare:Password? = null

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
                AsyncImage(model = data?.otherUserProfileUrl, contentDescription = "Profile image")
                Text(
                    text = "Your Password/Note will be shared with ${data?.otherUserPublicUname}",
                    color = MaterialTheme.colorScheme.error
                )
            },
            confirmButton = {
                Button(onClick = {
                    println("passwordItemToShare: sharedChatState.value.sharedPasswordItem: ${sharedChatState.value.sharedPasswordItem}")
                    passwordItemToShare = sharedChatState.value.sharedPasswordItem
                    isItemShared = false
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
                title = { Text(text = "Add Password") },
                navigationIcon = {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable {
//                            onSharedChatEvent(ChatEvent.resetSharedViewModel)
                            navigateToPasswordScreen()
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.Black)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    UserNameRow(
                        person = data,
                        modifier = Modifier.padding(
                            top = 60.dp,
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 20.dp
                        )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Color.White, RoundedCornerShape(
                                    topStart = 30.dp, topEnd = 30.dp
                                )
                            )
                            .padding(top = 25.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        LazyColumn(
                            modifier = Modifier.padding(
                                start = 15.dp,
                                top = 25.dp,
                                end = 15.dp,
                                bottom = 75.dp
                            ),
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
                    }
                }

                CustomTextField(
                    text = messageTextValue, onValueChange = {
                        onChatEvent(ChatMessageEvent.OnMessageEntered(it))
                    },
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                        .align(Alignment.BottomCenter),
                    onTrailingIconButtonClicked = {
                        onChatEvent(ChatMessageEvent.SendMessage(data?.commonChatRoomId!!, sharedChatState.value.sharedPasswordItem))
                        onSharedChatEvent(SharedChatEvent.SharePasswordItem(null))
                        onSharedChatEvent(SharedChatEvent.ShareNoteItem(null))
                    }
                )
            }
        }
    )
    // Scroll to the bottom of the LazyColumn when a new item is added
    LaunchedEffect(state.value) {
        if (chatMessages.isNullOrEmpty()) {
            //todo do nothing
        } else {
//            lazyColumnState.scrollToItem(1)
            lazyColumnState.animateScrollToItem(lazyColumnState.layoutInfo.totalItemsCount)
        }
    }
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
                    RoundedCornerShape(20f)
                ),
            horizontalAlignment = Alignment.End,
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
        Box(
            modifier = Modifier
                .background(
                    if (chat.publicUid != senderPublicUID) RedOrange else Pink80,
                    RoundedCornerShape(100.dp)
                ),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = chat.message ?: "", style = TextStyle(
                    color = Color.Black,
                    fontSize = 15.sp
                ),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 15.dp),
                textAlign = TextAlign.End
            )
        }

        Text(
            text = timeStampUtil.getTime(chat.dateAndTime!!),
            style = TextStyle(
                color = Color.Gray,
                fontSize = 12.sp
            ),
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 15.dp),
        )
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
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Color.Yellow)
        ) {
            Icon(Icons.Default.Add, contentDescription = "", modifier = Modifier.size(16.dp))
        }
        TextField(
            value = text, onValueChange = { onValueChange(it) },
            placeholder = {
                Text(
                    text = "Type",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black
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
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .weight(0.8f),
            maxLines = 5,
            shape = CircleShape
        )
        IconButton(
            onClick = {
                onTrailingIconButtonClicked()
            },
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(Color.Yellow)
        ) {
            Icon(Icons.Default.Send, contentDescription = "", modifier = Modifier.size(16.dp))
        }
    }

}


@Composable
fun UserNameRow(
    modifier: Modifier = Modifier,
    person: UserPersonalChatList? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        person?.let { personData ->
            Row {
                personData.otherUserProfileUrl?.let {
                    AsyncImage(
                        model = personData.otherUserProfileUrl,
                        contentDescription = "",
                        modifier = Modifier
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
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "Online", style = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    )
                }
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