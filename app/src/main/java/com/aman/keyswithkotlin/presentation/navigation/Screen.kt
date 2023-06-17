package com.aman.keyswithkotlin.presentation.navigation

import com.aman.keyswithkotlin.core.Constants.ADD_EDIT_EXPANSE_SCREEN
import com.aman.keyswithkotlin.core.Constants.AUTH_SCREEN
import com.aman.keyswithkotlin.core.Constants.HOME_SCREEN
import com.aman.keyswithkotlin.core.Constants.PROFILE_SCREEN

sealed class Screen(val route: String) {
    object AuthScreen : Screen(AUTH_SCREEN)
    object ProfileScreen : Screen(PROFILE_SCREEN)
}