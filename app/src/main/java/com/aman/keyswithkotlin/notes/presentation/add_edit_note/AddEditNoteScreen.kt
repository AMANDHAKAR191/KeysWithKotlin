package com.aman.keyswithkotlin.notes.presentation.add_edit_note

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.aman.keyswithkotlin.core.Constants
import com.aman.keyswithkotlin.notes.domain.model.Note
import com.aman.keyswithkotlin.notes.presentation.add_edit_note.components.TransparentHintTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    state: AddEditNoteState,
    eventFlow: SharedFlow<AddEditNoteViewModel.UiEvent>,
    onEvent: (AddEditNoteEvent) -> Unit,
    navigateToNoteScreen: () -> Unit
) {

    val snackBarHostState = remember { SnackbarHostState() }

    val noteBackgroundAnimatable = remember {
        Animatable(
            Color(if (state.color.toColorInt() != -1) state.color.toColorInt() else state.color.toColorInt())
        )
    }
    val scope = rememberCoroutineScope()

    // Define a separate lambda for handling back navigation
    val handleBackNavigation: () -> Unit = {
        scope.launch {
            delay(Constants.EXIT_DURATION.toLong()) // Adjust this to match your animation duration
            navigateToNoteScreen()
        }
    }
    LaunchedEffect(key1 = true) {
        eventFlow.collectLatest { event ->
            when (event) {
                is AddEditNoteViewModel.UiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(
                        message = event.message
                    )
                }

                is AddEditNoteViewModel.UiEvent.SaveNote -> {
                    handleBackNavigation()
                }
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            MediumTopAppBar(
                title = { Text(text = "Add Note") },
                navigationIcon = {
                    IconButton(onClick = {
                        handleBackNavigation()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "")
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(AddEditNoteEvent.SaveNote) }) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save note")
                    }
                },
                scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(noteBackgroundAnimatable.value)
                    .padding(innerPadding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .testTag("ColorBoxParent"),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Note.noteColors.forEach { color ->
                        val colorInt = color.toArgb()
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .shadow(15.dp, CircleShape)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = 3.dp,
                                    color = if (state.color.toColorInt() == colorInt) {
                                        Color.Black
                                    } else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    scope.launch {
                                        noteBackgroundAnimatable.animateTo(
                                            targetValue = Color(colorInt),
                                            animationSpec = tween(
                                                durationMillis = 500
                                            )
                                        )
                                    }
                                    onEvent(AddEditNoteEvent.ChangeColor(colorInt.toString()))
                                }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TransparentHintTextField(
                    text = state.noteTitle,
                    label = "Title",
                    hint = "Title",
                    onValueChange = {
                        onEvent(AddEditNoteEvent.EnteredTitle(it))
                    },
                    singleLine = true,
                    showIndicator = true,
                    textStyle = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                TransparentHintTextField(
                    text = state.noteBody,
                    label = "Content",
                    hint = "Write here...",
                    onValueChange = {
                        onEvent(AddEditNoteEvent.EnteredContent(it))
                    },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxHeight(),
                    minLines = 5,
                    showIndicator = false,
                )
            }
        }
    )
}


@Preview
@Composable
fun Preview() {
    val state = AddEditNoteState() // Provide the desired state object
    val eventFlow =
        remember { MutableSharedFlow<AddEditNoteViewModel.UiEvent>() } // Create an instance of MutableSharedFlow
    val onEvent: (AddEditNoteEvent) -> Unit = {} // Provide an empty lambda function for onEvent
    val navigateToNoteScreen: () -> Unit =
        {} // Provide an empty lambda function for navigateToNoteScreen

    AddEditNoteScreen(
        state = state,
        eventFlow = eventFlow,
        onEvent = onEvent,
        navigateToNoteScreen = navigateToNoteScreen
    )
}
