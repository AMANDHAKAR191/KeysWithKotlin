package com.aman.keyswithkotlin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.core.components.BottomBar
import com.aman.keyswithkotlin.setting.presentation.SettingScreen

fun NavGraphBuilder.settingNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = BottomBarScreen.Settings.route,
        route = Graph.SETTING
    ) {
        composable(BottomBarScreen.Settings.route) {
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