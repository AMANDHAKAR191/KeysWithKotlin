package com.aman.keyswithkotlin.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.auth.presentation.auth.AuthScreen
import com.aman.keyswithkotlin.auth.presentation.profile.ProfileScreen
import com.aman.keyswithkotlin.chats.presentation.ChatsScreen
import com.aman.keyswithkotlin.notes.presentation.NotesScreen
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.ShareGeneratedPasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.generate_password.GeneratePasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.password_screen.PasswordScreen
import com.aman.keyswithkotlin.presentation.BottomBarScreen

@Composable
@ExperimentalAnimationApi
fun RootNavGraph(
    navController: NavHostController
) {
    val sharedPasswordViewModel: ShareGeneratedPasswordViewModel = hiltViewModel()
    NavHost(
        navController = navController,
        startDestination = Screen.AuthScreen.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(
            route = Screen.AuthScreen.route
        ) {
            AuthScreen(
                navigateToProfileScreen = {
                    navController.popBackStack()
                    navController.navigate(Graph.HOME)
                },
                navigateToPasswordScreen = {
                    navController.popBackStack()
                    navController.navigate(BottomBarScreen.Home.route)
                }
            )
        }
        navigation(
            startDestination = BottomBarScreen.Home.route,
            route = Graph.HOME
        ){
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
                        navController.navigate(BottomBarScreen.Settings.route)
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
            composable(BottomBarScreen.Settings.route) {
                ProfileScreen(
                    navigateToAuthScreen = {
                        navController.popBackStack()
                        navController.navigate(Screen.AuthScreen.route)
                    },
                    navigateToPasswordScreen = {
                        navController.navigate(BottomBarScreen.Home.route) {
                            popUpTo(BottomBarScreen.Home.route)
                        }
                    }
                )
            }
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val DETAILS = "details_graph"
}