package com.aman.keyswithkotlin.navigation

import android.content.Context
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.aman.keyswithkotlin.chats.presentation.SharedChatViewModel
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.SharePasswordViewModel

@Composable
@ExperimentalAnimationApi
fun RootNavGraph(
    navController: NavHostController,
    lifecycleOwner: LifecycleOwner
) {
    val sharedPasswordViewModel: SharePasswordViewModel = hiltViewModel()
    val sharedChatViewModel: SharedChatViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Graph.AUTHENTICATION,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        authNavGraph(navController)
        profileNavGraph(navController)
        accessVerificationNavGraph(navController)
        passwordNavGraph(
            navController = navController,
            sharedPasswordViewModel = sharedPasswordViewModel
        )
        chatNavGraph(navController, sharedPasswordViewModel, sharedChatViewModel)
        noteNavGraph(navController, sharedPasswordViewModel)
        settingNavGraph(navController)
    }
}

object Graph {
    const val AUTHENTICATION = "auth_graph"
    const val PROFILE = "profile_graph"
    const val ACCESS_VERIFICATION = "access_verification"
    const val HOME = "home_graph"
    const val CHAT = "chat_graph"
    const val NOTE = "note_graph"
    const val SETTING = "setting_graph"
}