package com.aman.keys.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.autofill.AutofillManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keys.core.components.BottomBar
import com.aman.keys.setting.presentation.AppInfoScreen
import com.aman.keys.setting.presentation.SettingScreen
import com.aman.keys.setting.presentation.SettingViewModel
import com.aman.keys.setting.presentation.manage_devicces.ManageDevicesScreen
import com.aman.keys.setting.presentation.manage_devicces.ManageDevicesViewModel


fun NavGraphBuilder.settingNavGraph(
    context: Context,
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
                },
                openPrivacyPolicy = {
                    val open_privacy_policy = Intent(Intent.ACTION_VIEW)
                    open_privacy_policy.data =
                        Uri.parse("https://amandhakar.blogspot.com/2022/02/privacy-policy-keys.html")
                    context.startActivity(open_privacy_policy)
                },
                openTermsAndCondition = {
                    val open_privacy_policy = Intent(Intent.ACTION_VIEW)
                    open_privacy_policy.data =
                        Uri.parse("https://amandhakar.blogspot.com/2022/02/terms-conditions-keys.html")
                    context.startActivity(open_privacy_policy)
                },
                openContactUs = {
                    val openMail =
                        Intent(Intent.ACTION_VIEW, Uri.parse("mailto:amandhakar.keys@gmail.com"))
                    context.startActivity(openMail)
                },
                navigateToManageDevicesScreen = {
                    navController.navigate(Screen.ManageDevicesScreen.route)
                }
            )
        }
        composable(route = Screen.AppInfoScreen.route) {
            AppInfoScreen(appVersion = "2.0", navigateBack = {
                navController.popBackStack()
            })
        }
        composable(route = Screen.AppInfoScreen.route) {
            val viewModel: ManageDevicesViewModel = hiltViewModel()
            ManageDevicesScreen(
                _state = viewModel.state,
                onEvent = viewModel::onEvent,
                navigateBack = {
                    navController.popBackStack()
                })
        }
    }

}