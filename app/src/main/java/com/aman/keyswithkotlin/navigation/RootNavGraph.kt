package com.aman.keyswithkotlin.navigation

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
    mAutofillManager: AutofillManager
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
            sharedPasswordViewModel = sharedPasswordViewModel,
            sharedChatViewModel = sharedChatViewModel
        )
        chatNavGraph(navController, sharedPasswordViewModel, sharedChatViewModel)
        noteNavGraph(navController, sharedPasswordViewModel)
        settingNavGraph(navController, mAutofillManager)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EnterAnimation(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = slideIn(
            initialOffset = { IntOffset(-1000, 0) },  // slide in from right
            animationSpec = tween(ENTER_DURATION, easing = LinearOutSlowInEasing),
        ),
        exit = slideOut(
            targetOffset = { IntOffset(-1000, 0) },  // slide out to right
            animationSpec = tween(EXIT_DURATION, easing = LinearOutSlowInEasing),
        ),
        content = content,
        initiallyVisible = visible
    )
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EnterAnimationForProfileScreen(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(
            initialScale = 0.3f,
            animationSpec = tween(ENTER_DURATION, easing = FastOutSlowInEasing),
            transformOrigin = TransformOrigin(1f, 0f)
        ),
        exit = fadeOut() + scaleOut(
            targetScale = 0.3f,
            animationSpec = tween(EXIT_DURATION, easing = FastOutSlowInEasing),
            transformOrigin = TransformOrigin(1f, 0f)
        ),
        content = content,
        initiallyVisible = false
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EnterAnimationForFAB(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(
            initialScale = 0.3f,
            animationSpec = tween(ENTER_DURATION, easing = FastOutSlowInEasing),
            transformOrigin = TransformOrigin(1f, 1f)
        ),
        exit = fadeOut() + scaleOut(
            targetScale = 0.3f,
            animationSpec = tween(EXIT_DURATION, easing = FastOutSlowInEasing),
            transformOrigin = TransformOrigin(1f, 1f)
        ),
        content = content,
        initiallyVisible = false
    )
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