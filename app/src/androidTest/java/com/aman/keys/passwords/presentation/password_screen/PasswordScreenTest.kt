package com.aman.keys.passwords.presentation.password_screen

import UIEvents
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToString
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aman.keys.chats.presentation.chat_user_list.SharedChatEvent
import com.aman.keys.core.BiometricStatus
import com.aman.keys.passwords.domain.model.Password
import com.aman.keys.passwords.presentation.add_edit_password.PasswordEvent
import com.aman.keys.passwords.presentation.add_edit_password.SharedPasswordEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PasswordScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    private val dummyState =  MutableStateFlow(PasswordState()).asStateFlow()  // Initialize with your default state
    private val _isTutorialEnabled =  MutableStateFlow(String()).asStateFlow()  // Initialize with your default state
    private val dummySharedPasswordState = PasswordState()  // Initialize with your default state


    private val _dummyEventFlow = MutableSharedFlow<UIEvents>()
    private val dummyEventFlow = _dummyEventFlow.asSharedFlow()
    private val dummyOnEvent: (PasswordEvent) -> Unit = {}
    private val dummyOnSharedPasswordEvent: (SharedPasswordEvent) -> Unit = {}
    private val dummyOnChatPasswordEvent: (SharedChatEvent) -> Unit = {}
    private val dummyNavigateToNoteScreen: () -> Unit = {}
    private val biometricResponse = flowOf(BiometricStatus.ERROR)

    @Test
    fun `if password list is not empty and passwords are displayed`() {
        val dummyPassword1 = Password(
            userName = "test username1",
            password = "testPassword1",
            websiteName = "test websitename1",
            linkTo = emptyList(),
            timestamp = "",
            lastUsedTimeStamp = ""
        )
        val dummyPassword2 = Password(
            userName = "test username2",
            password = "testPassword2",
            websiteName = "test websitename2",
            linkTo = emptyList(),
            timestamp = "",
            lastUsedTimeStamp = ""
        )

        val searchedPasswords =  MutableStateFlow<List<Password>>(listOf(dummyPassword1, dummyPassword2))

        val dummyState = MutableStateFlow(PasswordState(passwords = listOf(dummyPassword1, dummyPassword2)))

        composeTestRule.setContent {
            PasswordScreen(
                _isTutorialEnabled = _isTutorialEnabled,
                _state = dummyState,
                eventFlowState =dummyEventFlow,
                searchedPasswordState = searchedPasswords.collectAsState(),
                onEvent =dummyOnEvent,
                onSharedPasswordEvent =dummyOnSharedPasswordEvent,
                onSharedChatEvent =dummyOnChatPasswordEvent,
                navigateToAddEditPasswordScreen = { /*TODO*/ },
                navigateToGeneratePasswordScreen = { /*TODO*/ },
                navigateToProfileScreen = { /*TODO*/ },
                navigateToChatUserListScreen = { /*TODO*/ },
                navigateToAccessVerificationScreen = { /*TODO*/ },
                unHidePasswordChar = { biometricResponse},
                closeApp = { /*TODO*/ }) {
            }
        }

        composeTestRule.onNodeWithText("test username1").assertIsDisplayed()
        composeTestRule.onNodeWithText("test username2").assertIsDisplayed()
    }

    @Test
    fun `if password list is empty and NoPasswordHelperText is displayed`() {

        val searchedPasswords =  MutableStateFlow<List<Password>>(listOf())

        val dummyState = MutableStateFlow(PasswordState(passwords = listOf()))

        composeTestRule.setContent {
            PasswordScreen(
                _isTutorialEnabled = _isTutorialEnabled,
                _state = dummyState,
                eventFlowState =dummyEventFlow,
                searchedPasswordState = searchedPasswords.collectAsState(),
                onEvent =dummyOnEvent,
                onSharedPasswordEvent =dummyOnSharedPasswordEvent,
                onSharedChatEvent =dummyOnChatPasswordEvent,
                navigateToAddEditPasswordScreen = { /*TODO*/ },
                navigateToGeneratePasswordScreen = { /*TODO*/ },
                navigateToProfileScreen = { /*TODO*/ },
                navigateToChatUserListScreen = { /*TODO*/ },
                navigateToAccessVerificationScreen = { /*TODO*/ },
                unHidePasswordChar = { biometricResponse},
                closeApp = { /*TODO*/ }) {
            }
        }

        composeTestRule.onNodeWithTag("NoPasswordHelperText").assertIsDisplayed()
    }

    @Test
    fun `if password item is clicked and password item screen displayed`() {
        val dummyPassword1 = Password(
            userName = "test username1",
            password = "testPassword1",
            websiteName = "test websitename1",
            linkTo = emptyList(),
            timestamp = "",
            lastUsedTimeStamp = ""
        )
        val dummyPassword2 = Password(
            userName = "test username2",
            password = "testPassword2",
            websiteName = "test websitename2",
            linkTo = emptyList(),
            timestamp = "",
            lastUsedTimeStamp = ""
        )

        val searchedPasswords =  MutableStateFlow<List<Password>>(listOf(dummyPassword1, dummyPassword2))

        val dummyState = MutableStateFlow(PasswordState(passwords = listOf(dummyPassword1, dummyPassword2))).asStateFlow()

        composeTestRule.setContent {
            PasswordScreen(
                _isTutorialEnabled = _isTutorialEnabled,
                _state = dummyState,
                eventFlowState =dummyEventFlow,
                searchedPasswordState = searchedPasswords.collectAsState(),
                onEvent =dummyOnEvent,
                onSharedPasswordEvent =dummyOnSharedPasswordEvent,
                onSharedChatEvent =dummyOnChatPasswordEvent,
                navigateToAddEditPasswordScreen = { /*TODO*/ },
                navigateToGeneratePasswordScreen = { /*TODO*/ },
                navigateToProfileScreen = { /*TODO*/ },
                navigateToChatUserListScreen = { /*TODO*/ },
                navigateToAccessVerificationScreen = { /*TODO*/ },
                unHidePasswordChar = { biometricResponse},
                closeApp = { /*TODO*/ }) {
            }
        }

        composeTestRule.onNodeWithText("test username1").performClick()
        composeTestRule.onNodeWithTag("ViewPassword").assertIsDisplayed()
    }

    @Test
    fun `if show showDialog is triggered and AuthorizationAlertDialog is displayed`(){
        val searchedPasswords =  MutableStateFlow<List<Password>>(listOf())
        composeTestRule.setContent {
            PasswordScreen(
                _isTutorialEnabled = _isTutorialEnabled,
                _state = dummyState,
                eventFlowState =dummyEventFlow,
                searchedPasswordState = searchedPasswords.collectAsState(),
                onEvent =dummyOnEvent,
                onSharedPasswordEvent =dummyOnSharedPasswordEvent,
                onSharedChatEvent =dummyOnChatPasswordEvent,
                navigateToAddEditPasswordScreen = { /*TODO*/ },
                navigateToGeneratePasswordScreen = { /*TODO*/ },
                navigateToProfileScreen = { /*TODO*/ },
                navigateToChatUserListScreen = { /*TODO*/ },
                navigateToAccessVerificationScreen = { /*TODO*/ },
                unHidePasswordChar = { biometricResponse},
                closeApp = { /*TODO*/ }) {
            }
        }
        _dummyEventFlow.tryEmit(UIEvents.ShowAlertDialog)
//        composeTestRule.waitForIdle()
        runBlocking { delay(1000) }
        println(composeTestRule.onRoot().printToString())
//        composeTestRule.onNodeWithTag("AuthorizationAlertDialog").assertIsDisplayed()
//        println(composeTestRule.onRoot().printToString())

    }
}