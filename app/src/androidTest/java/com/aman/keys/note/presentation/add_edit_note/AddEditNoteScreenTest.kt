package com.aman.keys.note.presentation.add_edit_note

import UIEvents
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aman.keys.notes.presentation.add_edit_note.AddEditNoteEvent
import com.aman.keys.notes.presentation.add_edit_note.AddEditNoteScreen
import com.aman.keys.notes.presentation.add_edit_note.AddEditNoteState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicBoolean

@RunWith(AndroidJUnit4::class)
class AddEditNoteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val dummyState = AddEditNoteState()  // Initialize with your default state
    private val _dummyEventFlow = MutableSharedFlow<UIEvents>()
    private val dummyEventFlow = _dummyEventFlow.asSharedFlow()
    private val dummyOnEvent: (AddEditNoteEvent) -> Unit = {}
    private val dummyNavigateToNoteScreen: () -> Unit = {}

    @Test
    fun checkTopAppBar_Displayed() {
        composeTestRule.setContent {
            AddEditNoteScreen(
                state = dummyState,
                eventFlow = dummyEventFlow,
                onEvent = dummyOnEvent,
                navigateToNoteScreen = dummyNavigateToNoteScreen
            )
        }
        composeTestRule.onNodeWithText("Add Note").assertExists()
    }

    @Test
    fun checkFloatingActionButton_Displayed() {
        composeTestRule.setContent {
            AddEditNoteScreen(
                state = dummyState,
                eventFlow = dummyEventFlow,
                onEvent = dummyOnEvent,
                navigateToNoteScreen = dummyNavigateToNoteScreen
            )
        }
        composeTestRule.onNodeWithContentDescription("Save note").assertExists()
    }

    @Test
    fun checkColorBoxes_Displayed() {
        composeTestRule.setContent {
            AddEditNoteScreen(
                state = dummyState,
                eventFlow = dummyEventFlow,
                onEvent = dummyOnEvent,
                navigateToNoteScreen = dummyNavigateToNoteScreen
            )
        }
        // Assume you have 5 colors to choose from
        composeTestRule.onAllNodes(hasParent(hasTestTag("ColorBoxParent"))).assertCountEquals(5)
    }

    @Test
    fun checkSnackbar_Displayed() {
        composeTestRule.setContent {
            AddEditNoteScreen(
                state = dummyState,
                eventFlow = dummyEventFlow,
                onEvent = dummyOnEvent,
                navigateToNoteScreen = dummyNavigateToNoteScreen
            )
        }
        // Trigger the event to show Snackbar
        _dummyEventFlow.tryEmit(UIEvents.ShowSnackBar("Test message"))
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Test message").assertExists()
    }

    @Test
    fun checkNavigateToNoteScreen_Triggered() {
        val navigationTriggered = AtomicBoolean(false)
        composeTestRule.setContent {
            AddEditNoteScreen(
                state = dummyState,
                eventFlow = dummyEventFlow,
                onEvent = dummyOnEvent,
                navigateToNoteScreen = { navigationTriggered.set(true) }
            )
        }
        // Trigger the event to navigate
        _dummyEventFlow.tryEmit(UIEvents.SaveNote)
        composeTestRule.waitForIdle()
        assertTrue(navigationTriggered.get())
    }



//    @Test
//    fun checkSnackbar_Displayed() {
//        composeTestRule.setContent {
//            AddEditNoteScreen(
//                state = dummyState,
//                eventFlow = dummyEventFlow,
//                onEvent = dummyOnEvent,
//                navigateToNoteScreen = dummyNavigateToNoteScreen
//            )
//        }
//        // Trigger the event to show Snackbar
//        dummyEventFlow.tryEmit(AddEditNoteViewModel.UiEvent.ShowSnackBar("Test message"))
//        composeTestRule.onNodeWithText("Test message").assertExists()
//    }

//    @Test
//    fun checkNavigateToNoteScreen_Triggered() {
//        var navigationTriggered = false
//        composeTestRule.setContent {
//            AddEditNoteScreen(
//                state = dummyState,
//                eventFlow = dummyEventFlow,
//                onEvent = dummyOnEvent,
//                navigateToNoteScreen = { navigationTriggered = true }
//            )
//        }
//        // Trigger the event to navigate
//        dummyEventFlow.tryEmit(AddEditNoteViewModel.UiEvent.SaveNote)
//        assertTrue(navigationTriggered)
//    }
}
