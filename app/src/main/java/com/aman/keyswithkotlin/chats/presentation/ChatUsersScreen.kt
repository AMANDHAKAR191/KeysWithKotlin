package com.aman.keyswithkotlin.chats.presentation

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.aman.keyswithkotlin.passwords.presentation.componants.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    title: String,
    chatUsersList: List<UserPersonalChatList>? = emptyList(),
    onEvent: (SharedChatEvent) -> Unit,
    bottomBar: @Composable (() -> Unit),
    navigateToChatScreen: () -> Unit
) {

    Scaffold(
        topBar = {
            TopBar(
                title = "Chats",
                onClickActionButton = {}
            )
        },
        modifier = Modifier.background(Color.Black),
        bottomBar = {
            bottomBar()
        }, content = { innerPadding ->
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
                    HeaderOrViewStory()
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
                                        onEvent(SharedChatEvent.OpenSharedChat(person))
                                        navigateToChatScreen()
                                    })
                                }
                            } else {
                                //todo wrote code to show loading bar
                            }
//                            items(personList, key = { it.id }) { person ->
//                                UserEachRow(person = person, onClick = {
//                                    onEvent(SharedChatEvent.OpenSharedChat(person))
//                                    navigateToChatScreen()
//                                })
//                            }
                        }
                    }
                }

            }
        }
    )
}

//@Preview
//@Composable
//fun Preview() {
//    ChatsScreen(title = "Aman", onEvent = {}, bottomBar = { /*TODO*/ }) {
//
//    }
//}

@Composable
fun HeaderOrViewStory() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
    ) {
        Header()
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
                    println("{person.otherUserProfileUrl}: ${person.otherUserProfileUrl}")
                    AsyncImage(
                        model = person.otherUserProfileUrl,
                        contentDescription = "",
                        modifier = Modifier
                            .clip(
                                CircleShape
                            ).background(Color.Yellow)
                            .padding(2.dp)
                            .clip(CircleShape)
                    )
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

@Composable
fun Header() {

    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.W300
            )
        ) {
            append("Welcome back")
        }
        withStyle(
            style = SpanStyle(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        ) {
            append("Aman")
        }
    }

    Text(text = annotatedString)

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