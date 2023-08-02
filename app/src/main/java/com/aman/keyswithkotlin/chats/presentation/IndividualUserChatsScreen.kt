package com.aman.keyswithkotlin.chats.presentation

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.aman.keyswithkotlin.ui.theme.Pink80
import com.aman.keyswithkotlin.ui.theme.RedOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualChatScreen(
    data: UserPersonalChatList? = null,
    state: ChatMessagesState,
//    _state: StateFlow<ChatMessagesState>,
    onChatEvent: (ChatEvent) -> Unit,
    navigateToPasswordScreen: () -> Unit
) {
//    val state = _state.collectAsState()
    val chatMessages:List<ChatModelClass>? = state.chatMessagesList
    val messageTextValue = state.chatMessage
    val lazyColumnState = rememberLazyListState()
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
                            if (!chatMessages.isNullOrEmpty()){
                                items(chatMessages){
                                    ChatRow(chat = it)
                                }
                            }
                        }
                    }
                }

                CustomTextField(
                    text = messageTextValue, onValueChange = {
                        onChatEvent(ChatEvent.OnMessageEntered(it))
                    },
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                        .align(Alignment.BottomCenter),
                    onTrailingIconButtonClicked = {
                        onChatEvent(ChatEvent.SendMessage)
                    }
                )
            }
        }
    )
    // Scroll to the bottom of the LazyColumn when a new item is added
    LaunchedEffect(chatMessages) {
        if (chatMessages.isNullOrEmpty()) {
            //todo do nothing
        } else {
            lazyColumnState.scrollToItem(1)
            lazyColumnState.animateScrollToItem(lazyColumnState.layoutInfo.totalItemsCount)
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

@Composable
fun ChatRow(
    chat: ChatModelClass
) {
    println("chat: ${chat.publicUid} :: message: ${chat.message}")
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (chat.publicUid == "kirandhaker123") Alignment.Start else Alignment.End
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (chat.publicUid == "kirandhaker123") RedOrange else Pink80,
                    RoundedCornerShape(100.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = chat.message!!, style = TextStyle(
                    color = Color.Black,
                    fontSize = 15.sp
                ),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 15.dp),
                textAlign = TextAlign.End
            )
        }
        Text(
            text = chat.dateAndTime!!,
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
        leadingIcon = {
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color.Yellow)
            ) {
                Icon(Icons.Default.Add, contentDescription = "", modifier = Modifier.size(16.dp))
            }
        },
        trailingIcon = {
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
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        modifier = modifier.fillMaxWidth(),
        maxLines = 5,
        shape = CircleShape
    )

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
        person?.let { personData->
            Row {
                AsyncImage(model = personData.otherUserProfileUrl ?: "", contentDescription = "")
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
