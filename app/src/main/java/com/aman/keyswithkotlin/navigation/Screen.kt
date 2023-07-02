package com.aman.keyswithkotlin.navigation

import com.aman.keyswithkotlin.core.Constants.ADD_EDIT_PASSWORD_SCREEN
import com.aman.keyswithkotlin.core.Constants.AUTH_SCREEN
import com.aman.keyswithkotlin.core.Constants.PROFILE_SCREEN

sealed class Screen(val route: String) {
    object AuthScreen : Screen(AUTH_SCREEN)
    object ProfileScreen : Screen(PROFILE_SCREEN)

    object AddEditPasswordScreen : Screen(ADD_EDIT_PASSWORD_SCREEN)
}