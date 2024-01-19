package com.aman.keys.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keys.core.components.BottomBar
import com.aman.keys.notes.presentation.add_edit_note.AddEditNoteScreen
import com.aman.keys.notes.presentation.add_edit_note.AddEditNoteViewModel
import com.aman.keys.notes.presentation.note_screen.NotesScreen
import com.aman.keys.notes.presentation.note_screen.NotesViewModel
import com.aman.keys.passwords.presentation.add_edit_password.SharePasswordViewModel

fun NavGraphBuilder.noteNavGraph(
    navController: NavController,
    sharedPasswordViewModel: SharePasswordViewModel
) {
    navigation(
        startDestination = BottomBarScreen.Notes.route,
        route = Graph.NOTE
    ) {
        composable(BottomBarScreen.Notes.route) {
            val viewModel: NotesViewModel = hiltViewModel()
            NotesScreen(
                _state = viewModel.state,
                eventFlowState = viewModel.eventFlow,
                onEvent = viewModel::onEvent,
                bottomBar = {
                    BottomBar(navController, navigateTo = {destScreen->
                        navController.popBackStack()
                        navController.navigate(destScreen) {
                            launchSingleTop = true
                        }
                    })
                },
                navigateToAddEditNoteScreen = {
                    navController.navigate(Screen.AddEditNoteScreen.route)
                })
        }
        composable(Screen.AddEditNoteScreen.route) {
            val viewModel: AddEditNoteViewModel = hiltViewModel()
            AddEditNoteScreen(
                state = viewModel.state.value,
                eventFlow = viewModel.eventFlow,
                onEvent = viewModel::onEvent,
                navigateToNoteScreen = {
                    navController.popBackStack()
                }
            )
        }
    }

}