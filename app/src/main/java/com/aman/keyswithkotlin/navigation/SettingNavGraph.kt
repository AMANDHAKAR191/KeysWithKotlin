package com.aman.keyswithkotlin.navigation

import android.view.autofill.AutofillManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.core.components.BottomBar
import com.aman.keyswithkotlin.setting.presentation.AppInfoScreen
import com.aman.keyswithkotlin.setting.presentation.SettingScreen
import com.aman.keyswithkotlin.setting.presentation.SettingViewModel

fun NavGraphBuilder.settingNavGraph(
    packageName: String,
    navController: NavController,
    mAutofillManager: AutofillManager
) {
    navigation(
        startDestination = BottomBarScreen.Settings.route,
        route = Graph.SETTING
    ) {
        composable(BottomBarScreen.Settings.route) {
            val viewModel: SettingViewModel = hiltViewModel()
            if (!mAutofillManager.hasEnabledAutofillServices()) {
                // Prompt the user to enable your service
            }
            SettingScreen(
                _state = viewModel.state,
                displayName = viewModel.displayName.invoke(),
                email = viewModel.email.invoke(),
                photoUrl = viewModel.photoUrl.invoke(),
                onEvent = viewModel::onEvent,
                eventFlowState = viewModel.eventFlow,
                autofillManager = mAutofillManager,
                packageName = packageName,
                bottomBar = {
                    BottomBar(navController, navigateTo = { destScreen ->
                        navController.navigate(destScreen) {
                            launchSingleTop = true
                        }
                    })
                },
                navigateToProfileScreen = {
                    navController.navigate(Graph.PROFILE)
                },
                navigateToAppInfoScreen = {
                    navController.navigate(Screen.AppInfoScreen.route)
                }
            )
        }
        composable(route = Screen.AppInfoScreen.route) {
            AppInfoScreen(appVersion = "2.0", navigateBack = {
                navController.popBackStack()
            })
        }
    }

}