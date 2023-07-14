package com.aman.keyswithkotlin.notes.presentation.note_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aman.keyswithkotlin.notes.presentation.note_screen.components.NoteItem
import com.aman.keyswithkotlin.passwords.presentation.componants.TopBar
import kotlinx.coroutines.launch

@Composable
fun NotesScreen(
    state: NotesState,
    onEvent: (NotesEvent) -> Unit,
    bottomBar: @Composable (() -> Unit),
    navigateToAddEditNoteScreen: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopBar(
                title = "Notes",
                onClickActionButton = {}
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navigateToAddEditNoteScreen()
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .padding(all = 20.dp),
                shape = FloatingActionButtonDefaults.shape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add note")
            }
        }, bottomBar = {
            bottomBar()
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.Black)
                    .padding(top = 10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            ) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(state.notes) { note ->
                        NoteItem(
                            note = note,
                            modifier = Modifier
                                .width(170.dp)
                                .padding(8.dp)
                                .clickable {
                                    //todo code for viewing the note
                                },
                            onDeleteClick = {
                                onEvent(NotesEvent.DeleteNote(note))

                                scope.launch {
                                    val result = snackBarHostState.showSnackbar(
                                        message = "Note deleted",
                                        actionLabel = "Undo",
                                        withDismissAction = true,
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        onEvent(NotesEvent.RestoreNote)
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
//                LazyColumn(modifier = Modifier.fillMaxSize()) {
//                    items(state.notes) { note ->
//                        NoteItem(
//                            note = note,
//                            modifier = Modifier
//                                .width(170.dp)
//                                .clickable {
//                                    //todo code for viewing the note
//                                },
//                            onDeleteClick = {
//                                onEvent(NotesEvent.DeleteNote(note))
//
//                                scope.launch {
//                                    val result = snackBarHostState.showSnackbar(
//                                        message = "Note deleted",
//                                        actionLabel = "Undo",
//                                        withDismissAction = true,
//                                        duration = SnackbarDuration.Short
//                                    )
//                                    if (result == SnackbarResult.ActionPerformed) {
//                                        onEvent(NotesEvent.RestoreNote)
//                                    }
//                                }
//                            }
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                    }
//                }
            }
        }
    )
}

