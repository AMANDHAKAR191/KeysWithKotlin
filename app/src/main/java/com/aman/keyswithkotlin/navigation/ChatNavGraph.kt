package com.aman.keyswithkotlin.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.chats.presentation.ChatUserViewModel
import com.aman.keyswithkotlin.chats.presentation.ChatsScreen
import com.aman.keyswithkotlin.chats.presentation.individual_chat.IndividualUserChatsViewModel
import com.aman.keyswithkotlin.chats.presentation.SharedChatViewModel
import com.aman.keyswithkotlin.chats.presentation.individual_chat.IndividualChatScreen
import com.aman.keyswithkotlin.core.components.BottomBar
import com.aman.keyswithkotlin.di.PublicUID
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.SharePasswordViewModel
import javax.inject.Inject

fun NavGraphBuilder.chatNavGraph(
    navController: NavController,
    sharedPasswordViewModel: SharePasswordViewModel,
    sharedChatViewModel: SharedChatViewModel,
) {


    navigation(
        startDestination = BottomBarScreen.Chats.route,
        route = Graph.CHAT
    ) {
        composable(BottomBarScreen.Chats.route) {
            val viewModel: ChatUserViewModel = hiltViewModel()
            ChatsScreen(
                title = BottomBarScreen.Chats.title,
                chatUsersList = viewModel.state.value.chatUsersList,
                eventFlowState = viewModel.eventFlow,
                onEvent = viewModel::onEvent,
                onSharedChatEvent = sharedChatViewModel::onEvent,
                bottomBar = {
                    BottomBar(navController, navigateTo = {
                        navController.popBackStack()
                        navController.navigate(it) {
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
                data = sharedChatViewModel.state.value.person,
                _state = viewModel.state,
                eventFlowState = viewModel.eventFlow,
                onChatEvent = viewModel::onEvent,
                navigateToPasswordScreen = {
                    navController.popBackStack()
                }
            )
        }
    }
}