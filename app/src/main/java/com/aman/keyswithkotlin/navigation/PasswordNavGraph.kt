package com.aman.keyswithkotlin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.chats.presentation.ChatsScreen
import com.aman.keyswithkotlin.notes.presentation.NotesScreen
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.ShareGeneratedPasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.generate_password.GeneratePasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.password_screen.PasswordScreen
import com.aman.keyswithkotlin.presentation.BottomBar
import com.aman.keyswithkotlin.presentation.BottomBarScreen

fun NavGraphBuilder.passwordNavGraph(
    navController: NavController,
    sharedPasswordViewModel: ShareGeneratedPasswordViewModel
) {
    navigation(
        startDestination = BottomBarScreen.Password.route,
        route = Graph.HOME
    ) {
        composable(BottomBarScreen.Password.route) {
            PasswordScreen(
                sharedPasswordViewModel = sharedPasswordViewModel,
                navigateToAddEditPasswordScreen = {
                    navController.navigate(Screen.AddEditPasswordScreen.route)
                },
                navigateToGeneratePasswordScreen = {
                    navController.navigate(Screen.GeneratePasswordScreen.route)
                },
                navigateToProfileScreen = {
                    navController.navigate(Graph.SETTING)
                },
                bottomBar = {
                    BottomBar(navController, navigateTo = {
                        navController.navigate(it) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    })
                }
            )
        }
        composable(Screen.AddEditPasswordScreen.route) { entry ->
            AddEditPasswordScreen(
                sharedPasswordViewModel = sharedPasswordViewModel,
                navigateToPasswordScreen = {
                    navController.navigate(BottomBarScreen.Password.route) {
                        popUpTo(BottomBarScreen.Password.route)
                    }
                },
                navigateToGeneratePasswordScreen = {
                    navController.navigate(Screen.GeneratePasswordScreen.route)
                }
            )
        }
        composable(Screen.GeneratePasswordScreen.route) {
            GeneratePasswordScreen(
                sharedPasswordViewModel = sharedPasswordViewModel,
                navigateToPasswordScreen = {
                    navController.popBackStack()
                },
                navigateToAddEditPasswordScreen = {
                    navController.navigate(Screen.AddEditPasswordScreen.route)
                }
            )
        }
    }

}