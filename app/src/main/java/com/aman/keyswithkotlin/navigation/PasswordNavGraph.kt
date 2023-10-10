package com.aman.keyswithkotlin.navigation

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.chats.presentation.SharedChatViewModel
import com.aman.keyswithkotlin.core.BiometricAuthentication
import com.aman.keyswithkotlin.core.components.BottomBar
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.SharePasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.generate_password.GeneratePasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.generate_password.GeneratePasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.generate_password.RecentGeneratePasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.password_screen.PasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.password_screen.PasswordViewModel

fun NavGraphBuilder.passwordNavGraph(
    navController: NavController,
    biometricAuthentication: BiometricAuthentication,
    sharedPasswordViewModel: SharePasswordViewModel,
    sharedChatViewModel: SharedChatViewModel
) {
    navigation(
        startDestination = BottomBarScreen.Password.route,
        route = Graph.HOME
    ) {
        composable(BottomBarScreen.Password.route) {
            val viewModel: PasswordViewModel = hiltViewModel()
            PasswordScreen(
                _state = viewModel.state,
                eventFlowState = viewModel.eventFlow,
                searchedPasswordState = viewModel.searchedPasswords.collectAsState(),
                onEvent = viewModel::onEvent,
                onSharedPasswordEvent = sharedPasswordViewModel::onEvent,
                onSharedChatEvent = sharedChatViewModel::onEvent,
                navigateToAddEditPasswordScreen = {
                    navController.navigate(Screen.AddEditPasswordScreen.route)
                },
                navigateToGeneratePasswordScreen = {
                    navController.navigate(Screen.GeneratePasswordScreen.route)
                },
                navigateToProfileScreen = {
                    navController.navigate(Graph.PROFILE)
                },
                navigateToChatUserListScreen = {
                    navController.navigate(BottomBarScreen.Chats.route)
                },
                bottomBar = {
                    BottomBar(navController, navigateTo = {destScreen->
                        navController.navigate(destScreen) {
                            launchSingleTop = true
                        }
                    })
                },
                navigateToAccessVerificationScreen = {
                    navController.popBackStack()
                    navController.navigate(Graph.ACCESS_VERIFICATION)
                },
                closeApp = {
                    //todo re-check to close
                    navController.popBackStack()
                },
                unHidePasswordChar = {
                    biometricAuthentication.launchBiometric()
                }
            )
        }
        composable(Screen.AddEditPasswordScreen.route) {
            val viewModel: AddEditPasswordViewModel = hiltViewModel()
            AddEditPasswordScreen(
                state = viewModel.state.value,
                eventFlow = viewModel.eventFlow,
                onEvent = viewModel::onEvent,
                onSharedPasswordEvent = sharedPasswordViewModel::onEvent,
                passwordItem = sharedPasswordViewModel.itemToEdit.value.passwordItem,
                generatedPassword = sharedPasswordViewModel.generatedPassword.value.generatedPassword,
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
                },
                navigateToGeneratedPasswordScreen = {
                    navController.navigate(Screen.RecentGeneratedPasswordScreen.route)
                }
            )
        }
        composable(Screen.RecentGeneratedPasswordScreen.route) {
            val viewModel: GeneratePasswordViewModel = hiltViewModel()
            RecentGeneratePasswordScreen(
                recentGeneratedPasswordList = viewModel.state.value.recentGeneratedPasswordList,
                navigateToGeneratePasswordScreen = {
                    navController.popBackStack()
                })
        }
    }

}