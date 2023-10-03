package com.aman.keyswithkotlin.navigation

import android.app.Activity
import android.content.Context
import android.view.autofill.AutofillManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.aman.keyswithkotlin.chats.presentation.SharedChatViewModel
import com.aman.keyswithkotlin.core.Constants.ENTER_DURATION
import com.aman.keyswithkotlin.core.Constants.EXIT_DURATION
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.SharePasswordViewModel

@Composable
@ExperimentalAnimationApi
fun RootNavGraph(
    navController: NavHostController,
    mAutofillManager: AutofillManager,
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
            sharedPasswordViewModel = sharedPasswordViewModel,
            sharedChatViewModel = sharedChatViewModel
        )
        chatNavGraph(navController, sharedPasswordViewModel, sharedChatViewModel, activity, context)
        noteNavGraph(navController, sharedPasswordViewModel)
        settingNavGraph(navController, mAutofillManager)
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