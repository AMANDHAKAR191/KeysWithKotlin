package com.aman.keyswithkotlin.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aman.keyswithkotlin.auth.presentation.auth.AuthScreen
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.ShareGeneratedPasswordViewModel
import com.aman.keyswithkotlin.presentation.BottomBarScreen

@Composable
@ExperimentalAnimationApi
fun RootNavGraph(
    navController: NavHostController
) {
    val sharedPasswordViewModel: ShareGeneratedPasswordViewModel = hiltViewModel()
    NavHost(
        navController = navController,
        startDestination = Graph.AUTHENTICATION,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        authNavGraph(navController)
        passwordNavGraph(
            navController = navController,
            sharedPasswordViewModel = sharedPasswordViewModel
        )
        settingNavGraph(navController)
    }
}

object Graph {
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val CHAT = "chat_graph"
    const val NOTE = "note_graph"
    const val SETTING = "setting_graph"
}