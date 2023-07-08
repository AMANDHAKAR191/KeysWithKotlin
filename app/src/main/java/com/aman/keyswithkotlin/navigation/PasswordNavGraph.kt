package com.aman.keyswithkotlin.navigation

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.ShareGeneratedPasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.generate_password.GeneratePasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.generate_password.GeneratePasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.password_screen.PasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.password_screen.PasswordViewModel
import com.aman.keyswithkotlin.core.components.BottomBar

fun NavGraphBuilder.passwordNavGraph(
    navController: NavController,
    sharedPasswordViewModel: ShareGeneratedPasswordViewModel
) {
    navigation(
        startDestination = BottomBarScreen.Password.route,
        route = Graph.HOME
    ) {
        composable(BottomBarScreen.Password.route) {
            val viewModel: PasswordViewModel = hiltViewModel()
            PasswordScreen(
                state = viewModel.state.value,
                eventFlowState = viewModel.eventFlow,
                searchedPasswordState = viewModel.searchedPasswords.collectAsState(),
                onEvent= viewModel::onEvent,
                onSharedPasswordEvent = sharedPasswordViewModel::onEvent,
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
                            launchSingleTop = true
                        }
                    })
                }
            )
        }
        composable(Screen.AddEditPasswordScreen.route) { entry ->
            val viewModel: AddEditPasswordViewModel = hiltViewModel()
            AddEditPasswordScreen(
                state = viewModel.state.value,
                eventFlow = viewModel.eventFlow,
                onEvent = viewModel::onEvent,
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
            val viewModel: GeneratePasswordViewModel = hiltViewModel()
            GeneratePasswordScreen(
                state = viewModel.state.value,
                onEvent = viewModel::onEvent,
                onSharedPasswordEvent = sharedPasswordViewModel::onEvent,
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