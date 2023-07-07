package com.aman.keyswithkotlin.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.auth.presentation.profile.ProfileScreen
import com.aman.keyswithkotlin.chats.presentation.ChatsScreen
import com.aman.keyswithkotlin.notes.presentation.NotesScreen
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.ShareGeneratedPasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.generate_password.GeneratePasswordScreen
import com.aman.keyswithkotlin.passwords.presentation.password_screen.PasswordScreen
import com.aman.keyswithkotlin.presentation.BottomBarScreen

fun NavGraphBuilder.noteNavGraph(
    navController: NavController,
    sharedPasswordViewModel: ShareGeneratedPasswordViewModel
) {
    navigation(
        startDestination = BottomBarScreen.Notes.route,
        route = Graph.NOTE
    ) {
        composable(BottomBarScreen.Notes.route) {
            NotesScreen()
        }
    }

}