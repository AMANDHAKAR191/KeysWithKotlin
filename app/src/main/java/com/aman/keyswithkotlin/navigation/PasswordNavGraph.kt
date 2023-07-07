package com.aman.keyswithkotlin.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.auth.presentation.profile.ProfileScreen
import com.aman.keyswithkotlin.chats.presentation.ChatsScreen
import com.aman.keyswithkotlin.notes.presentation.NotesScreen
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.ShareGeneratedPasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.generate_password.GeneratePasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.password_screen.PasswordScreen
import com.aman.keyswithkotlin.presentation.BottomBarScreen

fun NavGraphBuilder.passwordNavGraph(
    navController: NavController,
    sharedPasswordViewModel: ShareGeneratedPasswordViewModel
) {
    navigation(
        startDestination = BottomBarScreen.Home.route,
        route = Graph.HOME
    ) {
        composable(BottomBarScreen.Home.route) {
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
                }
            )
        }
        composable(Screen.AddEditPasswordScreen.route) { entry ->
            AddEditPasswordScreen(
                sharedPasswordViewModel = sharedPasswordViewModel,
                navigateToPasswordScreen = {
                    navController.navigate(BottomBarScreen.Home.route) {
                        popUpTo(BottomBarScreen.Home.route)
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

        composable(BottomBarScreen.Chats.route) {
            ChatsScreen()
        }
        composable(BottomBarScreen.Notes.route) {
            NotesScreen()
        }
//        composable(BottomBarScreen.Settings.route) {
//            ProfileScreen(
//                navigateToAuthScreen = {
//                    navController.popBackStack()
//                    navController.navigate(Screen.AuthScreen.route)
//                },
//                navigateToPasswordScreen = {
//                    navController.navigate(BottomBarScreen.Home.route) {
//                        popUpTo(BottomBarScreen.Home.route)
//                    }
//                }
//            )
//        }
    }

}