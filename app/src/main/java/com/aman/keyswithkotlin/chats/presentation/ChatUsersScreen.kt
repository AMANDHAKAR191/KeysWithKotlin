package com.aman.keyswithkotlin.chats.presentation

import UIEvents
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList
import com.aman.keyswithkotlin.core.Constants.EXIT_DURATION
import com.aman.keyswithkotlin.passwords.presentation.componants.TopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ChatsScreen(
    title: String,
    chatUsersList: List<UserPersonalChatList>? = emptyList(),
    eventFlowState: SharedFlow<UIEvents>,
    onEvent: (ChatUserEvent) -> Unit,
    onSharedChatEvent: (SharedChatEvent) -> Unit,
    bottomBar: @Composable (() -> Unit),
    navigateToChatScreen: () -> Unit,
) {

    var isDialogVisible by remember { mutableStateOf(false) }
    var isLoadingBarVisible by remember { mutableStateOf(false) }
    var otherUserPublicUid by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(key1 = true) {
        eventFlowState.collectLatest { event ->
            when (event) {
                UIEvents.ShowLoadingBar -> {
                    isLoadingBarVisible = true
                }

                UIEvents.ChatUsrCreatedSuccessFully -> {
                    isDialogVisible = false
                }

                is UIEvents.ShowError -> {
                    errorMessage = event.errorMessage
                    isLoadingBarVisible = false
                }

                else -> {}
            }
        }
    }
    Scaffold(
        topBar = {
            TopBar(
                title = "Chats",
                onClickActionButton = {}
            )
        },
        modifier = Modifier.background(Color.Black),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isDialogVisible = true;
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .padding(all = 20.dp),
                shape = FloatingActionButtonDefaults.shape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add note")
            }
        },
        bottomBar = {
            bottomBar()
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(innerPadding)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 30.dp)
                ) {
                    Header(title)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Color.White, RoundedCornerShape(
                                    topStart = 30.dp, topEnd = 30.dp
                                )
                            )
                    ) {
                        BottomSheetSwipeUp(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 15.dp)
                        )
                        LazyColumn(
                            modifier = Modifier.padding(bottom = 15.dp, top = 30.dp)
                        ) {
                            if (!chatUsersList.isNullOrEmpty()) {
                                items(items = chatUsersList) { person ->
                                    UserEachRow(person = person, onClick = {
                                        onSharedChatEvent(
                                            SharedChatEvent.OpenSharedChat(
                                                person,
                                                person.commonChatRoomId!!
                                            )
                                        )
                                        navigateToChatScreen()
                                    })
                                }
                            } else {
                                //todo wrote code to show loading bar
                            }
                        }
                    }
                }

            }

            //for Access Alert
            if (isDialogVisible) {
                println("check2::")
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog or on the back
                        // button. If you want to disable that functionality, simply use an empty
                        // onDismissRequest.
                    },
                    text = {
                        Column {
                            Text(text = "Public ID")
                            TextField(
                                value = otherUserPublicUid,
                                onValueChange = { otherUserPublicUid = it },
                                trailingIcon = {
                                    if (isLoadingBarVisible) {
                                        CircularProgressIndicator(modifier = Modifier.size(25.dp))
                                    }
                                })
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            onEvent(ChatUserEvent.CreateChatUser(otherUserPublicUid))
                        }) {
                            Text("Create Chat")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                isDialogVisible = false
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    )
}


@Composable
fun Header(userName:String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
    ) {
        val annotatedString = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W300
                )
            ) {
                append("Welcome back ")
            }
            withStyle(
                style = SpanStyle(
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            ) {
                append(userName)
            }
        }

        Text(text = annotatedString)
    }
}

@Composable
fun BottomSheetSwipeUp(
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .background(
                Color.Gray,
                RoundedCornerShape(90.dp)
            )
            .width(90.dp)
            .height(5.dp)

    )
}


@Composable
fun UserEachRow(
    person: UserPersonalChatList,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .noRippleEffect { onClick() }
            .padding(horizontal = 20.dp, vertical = 5.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    person.otherUserProfileUrl?.let {
                        AsyncImage(
                            model = it,
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
                            text = person.otherUserPublicUname!!, style = TextStyle(
                                color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold
                            )
                        )
                        SpacerHeight(5.dp)
                        Text(
                            text = "Okay", style = TextStyle(
                                color = Color.Gray, fontSize = 14.sp
                            )
                        )
                    }

                }
                Text(
                    text = "12:30 PM", style = TextStyle(
                        color = Color.Gray, fontSize = 12.sp
                    )
                )
            }
            SpacerHeight(15.dp)
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
        }
    }

}

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.noRippleEffect(onClick: () -> Unit) = composed {
    clickable(
        interactionSource = MutableInteractionSource(),
        indication = null
    ) {
        onClick()
    }
}