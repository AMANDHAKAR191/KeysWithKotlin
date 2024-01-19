package com.aman.keys.navigation

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keys.chats.presentation.chat_user_list.ChatUserViewModel
import com.aman.keys.chats.presentation.chat_user_list.ChatsScreen
import com.aman.keys.chats.presentation.individual_chat.IndividualUserChatsViewModel
import com.aman.keys.chats.presentation.chat_user_list.SharedChatViewModel
import com.aman.keys.chats.presentation.individual_chat.IndividualChatScreen
import com.aman.keys.core.components.BottomBar
import com.aman.keys.notification_service.FCMNotificationSender
import com.aman.keys.passwords.presentation.add_edit_password.SharePasswordViewModel

fun NavGraphBuilder.chatNavGraph(
    navController: NavController,
    sharedPasswordViewModel: SharePasswordViewModel,
    sharedChatViewModel: SharedChatViewModel,
    activity: Activity,
    context: Context
) {


    navigation(
        startDestination = BottomBarScreen.Chats.route,
        route = Graph.CHAT
    ) {
        composable(BottomBarScreen.Chats.route) {
            val viewModel: ChatUserViewModel = hiltViewModel()
            ChatsScreen(
                _state = viewModel.state,
                _isTutorialEnabled = viewModel.isTutorialEnabled,
                eventFlowState = viewModel.eventFlow,
                onEvent = viewModel::onEvent,
                onSharedChatEvent = sharedChatViewModel::onEvent,
                bottomBar = {
                    BottomBar(navController, navigateTo = {destScreen->
                        navController.popBackStack()
                        navController.navigate(destScreen) {
                            launchSingleTop = true
                        }
                    })
                },
                navigateToChatScreen = {
                    navController.navigate(Screen.IndividualChatScreen.route)
                }
            )
        }
        composable(Screen.IndividualChatScreen.route) {
            val viewModel: IndividualUserChatsViewModel = hiltViewModel()
            IndividualChatScreen(
                _state = viewModel.state,
                _sharedChatState = sharedChatViewModel.state,
                eventFlowState = viewModel.eventFlow,
                onChatEvent = viewModel::onEvent,
                onSharedChatEvent = sharedChatViewModel::onEvent,
                navigateBack = {
                    navController.popBackStack()
                },
                sendNotification = {otherUserPublicUid, senderPublicUid, body->
                    val notificationSender = FCMNotificationSender(
                        "/topics/$otherUserPublicUid",
                        senderPublicUid,
                        body,
                        context,
                        activity
                    )
                    notificationSender.sendNotification()
                    Toast.makeText(context, "Notification Sent", Toast.LENGTH_SHORT).show()

                }
            )
        }
    }
}