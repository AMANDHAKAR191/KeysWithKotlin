package com.aman.keyswithkotlin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.notes.presentation.note_screen.NotesScreen
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.ShareGeneratedPasswordViewModel
import com.aman.keyswithkotlin.core.components.BottomBar

fun NavGraphBuilder.noteNavGraph(
    navController: NavController,
    sharedPasswordViewModel: ShareGeneratedPasswordViewModel
) {
    navigation(
        startDestination = BottomBarScreen.Notes.route,
        route = Graph.NOTE
    ) {
        composable(BottomBarScreen.Notes.route) {
            NotesScreen(
                title = BottomBarScreen.Notes.title,
                bottomBar = {
                    BottomBar(navController, navigateTo = {
                        navController.popBackStack()
                        navController.navigate(it) {
                            launchSingleTop = true
                        }
                    })
                }
            )
        }
    }

}