package com.aman.keyswithkotlin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.aman.keyswithkotlin.auth.presentation.auth.AuthScreen
import com.aman.keyswithkotlin.auth.presentation.profile.ProfileScreen
import com.aman.keyswithkotlin.chats.presentation.ChatsScreen
import com.aman.keyswithkotlin.notes.presentation.NotesScreen
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.generate_password.GeneratePasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.password_screen.PasswordScreen
import com.aman.keyswithkotlin.presentation.BottomBarScreen

@Composable
fun NavGraphBuilder.PasswordNavGraph(navController: NavController) {
    navigation(
        route = Graph.HOME,
        startDestination = BottomBarScreen.Home.route
    ) {
//        composable(BottomBarScreen.Home.route) {
//            PasswordScreen(
//                navigateToAddEditPasswordScreen = {
//                    navController.navigate("${Screen.AddEditPasswordScreen.route}/$it")
//                },
//                navigateToGeneratePasswordScreen = {
//                    navController.navigate(Screen.GeneratePasswordScreen.route)
//                },
//                navigateToProfileScreen = {
//                    navController.navigate(BottomBarScreen.Settings.route)
//                }
//            )
//        }
//        composable("${Screen.AddEditPasswordScreen.route}/{generatedPassword}",
//            arguments = listOf(
//                navArgument("generatedPassword") {
//                    type = NavType.StringType
//                }
//            )) { entry ->
//            val param = entry.arguments?.getString("generatedPassword") ?: ""
//            AddEditPasswordScreen(
//                sharedPasswordViewModel = ,
//                navigateToPasswordScreen = {
//                    navController.navigate(BottomBarScreen.Home.route) {
//                        popUpTo(BottomBarScreen.Home.route)
//                    }
//                },
//                navigateToGeneratePasswordScreen = {
//                    navController.navigate(Screen.GeneratePasswordScreen.route)
//                }
//            )
//        }
//        composable(Screen.GeneratePasswordScreen.route) {
//            GeneratePasswordScreen(
//                navigateToPasswordScreen = {
//                    navController.popBackStack()
//                },
//                navigateToAddEditPasswordScreen = {
//                    navController.navigate("${Screen.AddEditPasswordScreen.route}/$it")
//                }
//            )
//        }
//
//        composable(BottomBarScreen.Chats.route) {
//            ChatsScreen()
//        }
//        composable(BottomBarScreen.Notes.route) {
//            NotesScreen()
//        }
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