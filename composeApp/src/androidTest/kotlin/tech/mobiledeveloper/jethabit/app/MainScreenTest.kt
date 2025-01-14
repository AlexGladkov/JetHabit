package tech.mobiledeveloper.jethabit.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tech.mobiledeveloper.jethabit.app.MainActivity

@RunWith(AndroidJUnit4::class)
class MainScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appLaunchTest() {
        // Wait for the app to launch
        composeTestRule.waitForIdle()
        
        // Verify that the app launches with some expected initial content
        // You can modify these assertions based on your actual UI
        composeTestRule.onNodeWithText("Daily", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Stats", useUnmergedTree = true).assertIsDisplayed()
    }
} 