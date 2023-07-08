package com.aman.keyswithkotlin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.chats.presentation.ChatsScreen
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.ShareGeneratedPasswordViewModel
import com.aman.keyswithkotlin.core.components.BottomBar

fun NavGraphBuilder.chatNavGraph(
    navController: NavController,
    sharedPasswordViewModel: ShareGeneratedPasswordViewModel
) {
    navigation(
        startDestination = BottomBarScreen.Chats.route,
        route = Graph.CHAT
    ) {
        composable(BottomBarScreen.Chats.route) {
            ChatsScreen(
                title = BottomBarScreen.Chats.title,
                bottomBar = {
                    BottomBar(navController, navigateTo = {
                        navController.popBackStack()
                        navController.navigate(it) {
                            launchSingleTop = true
                        }
                    })
                }
            )
        }
    }
}