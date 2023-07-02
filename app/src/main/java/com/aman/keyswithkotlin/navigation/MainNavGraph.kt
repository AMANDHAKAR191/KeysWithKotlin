package com.aman.keyswithkotlin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aman.keyswithkotlin.auth.presentation.profile.ProfileScreen
import com.aman.keyswithkotlin.chats.presentation.ChatsScreen
import com.aman.keyswithkotlin.notes.presentation.NotesScreen
import com.aman.keyswithkotlin.passwords.presentation.PasswordScreen
import com.aman.keyswithkotlin.presentation.BottomBarScreen

@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBarScreen.Home.route
    ) {

    }
}