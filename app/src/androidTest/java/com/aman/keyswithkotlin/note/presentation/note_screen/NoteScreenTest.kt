package com.aman.keyswithkotlin.note.presentation.note_screen

import UIEvents
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aman.keyswithkotlin.notes.domain.model.Note
import com.aman.keyswithkotlin.notes.presentation.add_edit_note.AddEditNoteEvent
import com.aman.keyswithkotlin.notes.presentation.add_edit_note.AddEditNoteState
import com.aman.keyswithkotlin.notes.presentation.add_edit_note.AddEditNoteViewModel
import com.aman.keyswithkotlin.notes.presentation.note_screen.NotesEvent
import com.aman.keyswithkotlin.notes.presentation.note_screen.NotesScreen
import com.aman.keyswithkotlin.notes.presentation.note_screen.NotesState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteScreenTest {


    @get:Rule
    val composeTestRule = createComposeRule()

    private val _dummyState = MutableStateFlow(NotesState())  // Initialize with your default state
    private val dummyState = _dummyState.asStateFlow()  // Initialize with your default state
    private val _dummyEventFlow = MutableSharedFlow<UIEvents>()
    private val dummyEventFlow = _dummyEventFlow.asSharedFlow()
    private val dummyOnEvent: (NotesEvent) -> Unit = {}
    private val dummyNavigateToNoteScreen: () -> Unit = {}

    @Test
    fun checkIfSnackbarAppears() {
        val snackBarText = "Test Snack"
        composeTestRule.setContent {
            NotesScreen(
                _state = dummyState,
                eventFlowState = dummyEventFlow,
                onEvent = dummyOnEvent,
                bottomBar = {},
                navigateToAddEditNoteScreen = dummyNavigateToNoteScreen
            )
        }
        // Emit Snackbar Event
        _dummyEventFlow.tryEmit(UIEvents.ShowSnackBar(snackBarText, false))
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(snackBarText).assertExists()
    }

    @Test
    fun checkFloatingActionButtonExists() {
        composeTestRule.setContent {
            NotesScreen(
                _state = dummyState,
                eventFlowState = dummyEventFlow,
                onEvent = {},
                bottomBar = {},
                navigateToAddEditNoteScreen = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Add note").assertIsDisplayed()
    }

    @Test
    fun checkNotesAreDisplayed() {
        val dummyNote = Note("Test Title1", "Test Content1")
        val dummyNote1 = Note("Test Title2", "Test Content2")
        val dummyState = MutableStateFlow(NotesState(notes = listOf(dummyNote, dummyNote1))).asStateFlow()

        composeTestRule.setContent {
            NotesScreen(
                _state = dummyState,
                eventFlowState = dummyEventFlow,
                onEvent = {},
                bottomBar = {},
                navigateToAddEditNoteScreen = {}
            )
        }

        composeTestRule.onNodeWithText("Test Title1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Content1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Title2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Content2").assertIsDisplayed()
    }

}