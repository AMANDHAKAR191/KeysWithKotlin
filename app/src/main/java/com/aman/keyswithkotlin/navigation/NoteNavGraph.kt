package com.aman.keyswithkotlin.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.core.components.BottomBar
import com.aman.keyswithkotlin.notes.presentation.add_edit_note.AddEditNoteScreen
import com.aman.keyswithkotlin.notes.presentation.add_edit_note.AddEditNoteViewModel
import com.aman.keyswithkotlin.notes.presentation.note_screen.NotesScreen
import com.aman.keyswithkotlin.notes.presentation.note_screen.NotesViewModel
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.SharePasswordViewModel

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
                    BottomBar(navController, navigateTo = {destScreen, intialIndex, destIndex->
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