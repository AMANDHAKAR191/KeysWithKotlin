package com.aman.keyswithkotlin.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CustomCircularProgressBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testProgressBar_Displayed() {
        composeTestRule.setContent {
            CustomCircularProgressBar()
        }
        // Assert LottieAnimation is displayed. Replace 'LottieAnimation' with a testTag if you've set one.
        composeTestRule.onNodeWithTag("LottieAnimation").assertExists()
    }

    @Test
    fun testProgressBarWithStatus_Displayed() {
        composeTestRule.setContent {
            CustomCircularProgressBar(showStatus = true, status = "Loading")
        }
        // Assert Text is displayed
        composeTestRule.onNodeWithText("Loading").assertExists()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testProgressBarWithMissingStatus_ThrowsException() {
        composeTestRule.setContent {
            CustomCircularProgressBar(showStatus = true)
        }
    }
}