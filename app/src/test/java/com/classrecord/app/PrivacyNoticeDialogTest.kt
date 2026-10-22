package com.classrecord.app

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.classrecord.app.ui.privacy.PrivacyNoticeDialog
import com.classrecord.app.ui.theme.ClassRecordTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], qualifiers = "zh-rCN")
class PrivacyNoticeDialogTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun scrimBlocksClicksToContentBehind() {
        var behindClicked = false
        composeRule.setContent {
            ClassRecordTheme {
                Box(Modifier.fillMaxSize()) {
                    Button(
                        onClick = { behindClicked = true },
                        modifier = Modifier.testTag("behind_button"),
                    ) {
                        Text("Behind")
                    }
                    PrivacyNoticeDialog(
                        onDismiss = {},
                        onReadFullPolicy = {},
                    )
                }
            }
        }
        composeRule.onNodeWithText("我知道了").assertIsDisplayed()
        composeRule.onNodeWithTag("behind_button").performClick()
        composeRule.waitForIdle()
        assertFalse("Scrim should block touches to content behind the overlay", behindClicked)
    }

    @Test
    fun systemBackInvokesDismiss() {
        var dismissed = false
        composeRule.setContent {
            ClassRecordTheme {
                PrivacyNoticeDialog(
                    onDismiss = { dismissed = true },
                    onReadFullPolicy = {},
                )
            }
        }
        composeRule.onNodeWithText("我知道了").assertIsDisplayed()
        composeRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeRule.waitForIdle()
        assertTrue("Back should invoke the same dismiss callback as 我知道了", dismissed)
    }
}
