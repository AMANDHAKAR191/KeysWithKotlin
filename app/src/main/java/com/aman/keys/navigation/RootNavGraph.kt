package com.aman.keys.navigation

import android.app.Activity
import android.content.Context
import android.view.autofill.AutofillManager
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.aman.keys.chats.presentation.chat_user_list.SharedChatViewModel
import com.aman.keys.core.BiometricAuthentication
import com.aman.keys.passwords.presentation.add_edit_password.SharePasswordViewModel

@Composable
@ExperimentalAnimationApi
fun RootNavGraph(
    navController: NavHostController,
    mAutofillManager: AutofillManager,
    biometricAuthentication: BiometricAuthentication,
    activity: Activity,
    context: Context
) {
    val sharedPasswordViewModel: SharePasswordViewModel = hiltViewModel()
    val sharedChatViewModel: SharedChatViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Graph.AUTHENTICATION,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        onBoardingGraph(navController)
        authNavGraph(navController)
        profileNavGraph(navController)
        accessVerificationNavGraph(navController)
        passwordNavGraph(
            navController = navController,
            biometricAuthentication = biometricAuthentication,
            sharedPasswordViewModel = sharedPasswordViewModel,
            sharedChatViewModel = sharedChatViewModel
        )
        chatNavGraph(navController, sharedPasswordViewModel, sharedChatViewModel, activity, context)
        noteNavGraph(navController, sharedPasswordViewModel)
        settingNavGraph(context = context, packageName = activity.packageName, navController, mAutofillManager)
    }
}
object Graph {
    const val ON_BOARDING = "on_boarding_graph"
    const val AUTHENTICATION = "auth_graph"
    const val PROFILE = "profile_graph"
    const val ACCESS_VERIFICATION = "access_verification"
    const val HOME = "home_graph"
    const val CHAT = "chat_graph"
    const val NOTE = "note_graph"
    const val SETTING = "setting_graph"
}