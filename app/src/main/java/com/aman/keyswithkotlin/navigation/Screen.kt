package com.aman.keyswithkotlin.navigation

import com.aman.keyswithkotlin.core.Constants.AUTH_SCREEN
import com.aman.keyswithkotlin.core.Constants.PASSWORD_SCREEN
import com.aman.keyswithkotlin.core.Constants.PROFILE_SCREEN

sealed class Screen(val route: String) {
    object AuthScreen : Screen(AUTH_SCREEN)
    object ProfileScreen : Screen(PROFILE_SCREEN)
    object PasswordScreen : Screen(PASSWORD_SCREEN)
}