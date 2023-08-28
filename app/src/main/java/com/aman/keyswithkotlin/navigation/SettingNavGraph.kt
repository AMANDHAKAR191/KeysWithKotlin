package com.aman.keyswithkotlin.navigation

import android.content.Context
import android.view.autofill.AutofillManager
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.core.components.BottomBar
import com.aman.keyswithkotlin.setting.presentation.SettingScreen

fun NavGraphBuilder.settingNavGraph(
    navController: NavController,
    mAutofillManager: AutofillManager
) {
    navigation(
        startDestination = BottomBarScreen.Settings.route,
        route = Graph.SETTING
    ) {
        composable(BottomBarScreen.Settings.route) {
            if (!mAutofillManager.hasEnabledAutofillServices()) {
                // Prompt the user to enable your service
            }
            SettingScreen(
                bottomBar = {
                    BottomBar(navController, navigateTo = {
                        navController.navigate(it) {
                            launchSingleTop = true
                        }
                    })
                },
                navigateToProfileScreen = {
                    navController.navigate(Graph.PROFILE)
                }
            )
        }
    }

}