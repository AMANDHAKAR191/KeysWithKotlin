package com.aman.keyswithkotlin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.chats.domain.model.Person
import com.aman.keyswithkotlin.chats.presentation.ChatsScreen
import com.aman.keyswithkotlin.chats.presentation.IndividualChatScreen
import com.aman.keyswithkotlin.chats.presentation.SharedChatViewModel
import com.aman.keyswithkotlin.core.components.BottomBar
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.SharePasswordViewModel

fun NavGraphBuilder.chatNavGraph(
    navController: NavController,
    sharedPasswordViewModel: SharePasswordViewModel,
    sharedChatViewModel: SharedChatViewModel
) {
    navigation(
        startDestination = BottomBarScreen.Chats.route,
        route = Graph.CHAT
    ) {
        composable(BottomBarScreen.Chats.route) {
            navController.previousBackStackEntry?.savedStateHandle?.get<Person>("data") ?: Person()
            ChatsScreen(
                title = BottomBarScreen.Chats.title,
                onEvent = sharedChatViewModel::onEvent,
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
            IndividualChatScreen(
                data = sharedChatViewModel.state.value.person!!,
                onSharedChatEvent = sharedChatViewModel::onEvent,
                navigateToPasswordScreen = {
                    navController.popBackStack()
                }
            )
        }
    }
}