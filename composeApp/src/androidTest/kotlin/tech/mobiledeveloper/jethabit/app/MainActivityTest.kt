package tech.mobiledeveloper.jethabit.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAppLaunchesSuccessfully() {
        // Just wait for the app to launch and verify no crashes
        composeTestRule.waitForIdle()
        // If we reach this point, the app launched successfully without crashing
    }
} 