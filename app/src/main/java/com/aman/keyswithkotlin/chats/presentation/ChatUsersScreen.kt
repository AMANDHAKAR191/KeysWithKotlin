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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList
import com.aman.keyswithkotlin.core.util.TutorialType
import com.aman.keyswithkotlin.passwords.presentation.componants.TopBar
import com.aman.keyswithkotlin.presentation.ShowCaseProperty
import com.aman.keyswithkotlin.presentation.ShowCaseView
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    _state: StateFlow<ChatUserState>,
    _isTutorialEnabled: StateFlow<String>,
    eventFlowState: SharedFlow<UIEvents>,
    onEvent: (ChatUserEvent) -> Unit,
    onSharedChatEvent: (SharedChatEvent) -> Unit,
    bottomBar: @Composable (() -> Unit),
    navigateToChatScreen: () -> Unit,
) {
    val targets = remember { mutableStateMapOf<String, ShowCaseProperty>() }
    val state = _state.collectAsState()
    val isTutorialEnabled = _isTutorialEnabled.collectAsState()
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
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = "Chat")
            },
            colors = TopAppBarDefaults.topAppBarColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }, modifier = Modifier, floatingActionButton = {
        FloatingActionButton(onClick = {
            isDialogVisible = true;
        },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    targets["FAB"] = ShowCaseProperty(
                        index = 1,
                        coordinate = coordinates,
                        title = "Create Chat",
                        subTitle = "Click here!! to create new chat"
                    )
                }
                .padding(all = 20.dp),
            shape = FloatingActionButtonDefaults.shape) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add note")
        }
    }, bottomBar = {
        bottomBar()
    }, content = { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Header(state.value.username)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(
                            topStart = 30.dp, topEnd = 30.dp
                        )
                    )
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .padding(top = 30.dp, start = 20.dp, end = 20.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(bottom = 15.dp, top = 30.dp)
                ) {
                    if (!state.value.chatUsersList.isNullOrEmpty()) {
                        items(items = state.value.chatUsersList!!) { person ->
                            UserEachRow(person = person, onClick = {
                                onSharedChatEvent(
                                    SharedChatEvent.OpenSharedChat(
                                        person, person.commonChatRoomId!!
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

        //for Access Alert
        if (isDialogVisible) {
            println("check2::")
            AlertDialog(onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
            }, text = {
                Column {
                    Text(text = "Public ID")
                    TextField(value = otherUserPublicUid,
                        onValueChange = { otherUserPublicUid = it },
                        trailingIcon = {
                            if (isLoadingBarVisible) {
                                CircularProgressIndicator(modifier = Modifier.size(25.dp))
                            }
                        })
                    Text(
                        text = errorMessage, color = MaterialTheme.colorScheme.error
                    )
                }
            }, confirmButton = {
                Button(onClick = {
                    onEvent(ChatUserEvent.CreateChatUser(otherUserPublicUid))
                }) {
                    Text("Create Chat")
                }
            }, dismissButton = {
                TextButton(onClick = {
                    isDialogVisible = false
                }) {
                    Text("Cancel")
                }
            })
        }
    })

    if (isTutorialEnabled.value == TutorialType.ENABLED.toString()) {
        println("my check115")
        ShowCaseView(targets = targets) {
            onEvent(ChatUserEvent.DisableTutorial)
        }
    }
}


@Composable
fun Header(userName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
    ) {
        val annotatedString = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W300
                )
            ) {
                append("Welcome back ")
            }
            println("Header.userName: $userName")
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.W700,
                    fontSize = 20.sp,
                )
            ) {
                append(userName)
            }
        }

        Text(text = annotatedString, modifier = Modifier.onGloballyPositioned { })
    }
}


@Composable
fun UserEachRow(
    person: UserPersonalChatList, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleEffect { onClick() },
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    person.otherUserProfileUrl?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(55.dp)
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
                            text = person.otherUserPublicUid!!, style = TextStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 15.sp, fontWeight = FontWeight.Bold
                            )
                        )
                        SpacerHeight(5.dp)
                        Text(
                            text = person.otherUserPublicUname!!, style = TextStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp
                            )
                        )
                    }

                }
                Text(
                    text = "12:30 PM", style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp
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
        interactionSource = MutableInteractionSource(), indication = null
    ) {
        onClick()
    }
}