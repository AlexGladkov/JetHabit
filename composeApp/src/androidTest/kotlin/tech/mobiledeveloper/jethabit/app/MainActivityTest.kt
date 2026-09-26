package tech.mobiledeveloper.jethabit.app

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import feature.habits.data.HabitEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertNotSame
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import core.platform.AndroidImagePicker
import data.features.settings.SettingsEventBus
import java.util.concurrent.atomic.AtomicReference
import di.PlatformConfiguration
import di.PlatformSDK
import navigation.AppScreens
import navigation.AppTestTags
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appLaunchNavigatesFromSplashToMainDailyStart() {
        composeTestRule.waitUntilBottomNavigationExists()

        composeTestRule.assertExpectedNodeDisplayed(
            testTag = AppTestTags.BottomNavigation,
            expectedRoutes = bottomNavigationRoutes
        )
        composeTestRule.assertExpectedNodeDisplayed(
            testTag = AppTestTags.bottomNavigationItem(AppScreens.Daily.title),
            useUnmergedTree = true,
            expectedRoutes = bottomNavigationRoutes
        )
    }

    @Test
    fun currentActivityAndPickerAfterRecreation() {
        composeTestRule.waitUntilBottomNavigationExists()
        val before = MainActivity.lastCreatedActivity
        assertNotNull(before)
        val beforePicker = MainActivity.lastImagePicker
        assertNotNull(beforePicker)
        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitUntilBottomNavigationExists()
        val after = MainActivity.lastCreatedActivity
        assertNotNull(after)
        assertNotSame(before, after)
        assertSame(after, MainActivity.lastImagePicker?.ownerActivity)
        assertNotSame(before, MainActivity.lastImagePicker?.ownerActivity)
        assertNotSame(beforePicker, MainActivity.lastImagePicker)
        val currentConfiguration = PlatformSDK.instance<PlatformConfiguration>()
        assertSame(after, currentConfiguration.activity)
        assertSame(after, (currentConfiguration.imagePicker as AndroidImagePicker).ownerActivity)
        assertSame(MainActivity.lastImagePicker, currentConfiguration.imagePicker)
        composeTestRule.onNodeWithTag(AppTestTags.BottomNavigation).assertIsDisplayed()
    }

    @Test
    fun roomRowPersistsAcrossRecreation() {
        composeTestRule.waitUntilBottomNavigationExists()
        val application = (composeTestRule.activity as MainActivity).application as JetHabitApp
        val dbBefore = application.database
        val row = HabitEntity("acceptance", "Acceptance", true, "2024-01-01", "2024-12-31", "1")
        runBlocking { dbBefore.getHabitDao().insert(row) }
        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitUntilBottomNavigationExists()
        val applicationAfter = (composeTestRule.activity as MainActivity).application as JetHabitApp
        assertSame(application, applicationAfter)
        assertSame(dbBefore, applicationAfter.database)
        assertEquals("Acceptance", runBlocking { applicationAfter.database.getHabitDao().getHabitWith("acceptance").title })
    }

    @Test
    fun singleCompositionBootstrap() {
        composeTestRule.waitUntilBottomNavigationExists()
        val app = (composeTestRule.activity as MainActivity).application as JetHabitApp
        val settingsEventBusBeforeRecreation = app.settingsEventBus
        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitUntilBottomNavigationExists()
        val appAfterRecreation = (composeTestRule.activity as MainActivity).application as JetHabitApp
        assertEquals(1, PlatformSDK.initializationCount)
        assertSame(app, appAfterRecreation)
        assertSame(settingsEventBusBeforeRecreation, appAfterRecreation.settingsEventBus)
    }

    @Test
    fun settingsEventBusProductionUpdate() {
        composeTestRule.waitUntilBottomNavigationExists()
        val app = (composeTestRule.activity as MainActivity).application as JetHabitApp
        val productionBus = app.settingsEventBus
        val before = productionBus.currentSettings.value.isDarkMode
        val observedBus = AtomicReference<SettingsEventBus?>()
        val observedDarkMode = AtomicReference<Boolean?>()

        MainActivity.settingsEventBusObserverForTesting = { bus ->
            observedBus.set(bus)
            observedDarkMode.set(bus.currentSettings.value.isDarkMode)
        }
        try {
            composeTestRule.activityRule.scenario.recreate()
            composeTestRule.waitUntilBottomNavigationExists()
            composeTestRule.waitUntil {
                observedBus.get() === productionBus && observedDarkMode.get() == before
            }
            assertSame(productionBus, observedBus.get())

            productionBus.updateDarkMode(!before)
            composeTestRule.waitUntil {
                observedBus.get() === productionBus && observedDarkMode.get() == !before
            }
            assertEquals(!before, observedDarkMode.get())
        } finally {
            productionBus.updateDarkMode(before)
            MainActivity.settingsEventBusObserverForTesting = null
        }
    }

    @Test
    fun bottomNavigationItemsExposeStableTags() {
        composeTestRule.waitUntilBottomNavigationExists()

        bottomNavigationRoutes.forEach { route ->
            composeTestRule.assertExpectedNodeDisplayed(
                testTag = AppTestTags.bottomNavigationItem(route),
                useUnmergedTree = true,
                expectedRoutes = bottomNavigationRoutes
            )
        }
    }

    private fun AndroidComposeTestRule<*, *>.waitUntilBottomNavigationExists() {
        runWithDiagnostics(
            actionDescription = "wait for bottom navigation",
            expectedTags = listOf(AppTestTags.BottomNavigation),
            expectedRoutes = bottomNavigationRoutes
        ) {
            waitUntil {
                onAllNodesWithTag(AppTestTags.BottomNavigation).fetchSemanticsNodes().isNotEmpty()
            }
        }
    }

    private fun AndroidComposeTestRule<*, *>.assertExpectedNodeDisplayed(
        testTag: String,
        useUnmergedTree: Boolean = false,
        expectedRoutes: List<String>
    ) {
        runWithDiagnostics(
            actionDescription = "assert displayed tag=$testTag useUnmergedTree=$useUnmergedTree",
            expectedTags = listOf(testTag),
            expectedRoutes = expectedRoutes
        ) {
            onNodeWithTag(testTag = testTag, useUnmergedTree = useUnmergedTree).assertIsDisplayed()
        }
    }

    private fun AndroidComposeTestRule<*, *>.runWithDiagnostics(
        actionDescription: String,
        expectedTags: List<String>,
        expectedRoutes: List<String>,
        block: AndroidComposeTestRule<*, *>.() -> Unit
    ) {
        try {
            block()
        } catch (throwable: Throwable) {
            logSemanticsDiagnostics(
                actionDescription = actionDescription,
                expectedTags = expectedTags,
                expectedRoutes = expectedRoutes
            )
            throw throwable
        }
    }

    private fun AndroidComposeTestRule<*, *>.logSemanticsDiagnostics(
        actionDescription: String,
        expectedTags: List<String>,
        expectedRoutes: List<String>
    ) {
        Log.e(LOG_TAG, "Compose smoke failed while trying to $actionDescription")
        Log.e(LOG_TAG, "Expected tags: ${expectedTags.joinToString()}")
        Log.e(LOG_TAG, "Expected routes: ${expectedRoutes.joinToString()}")
        Log.e(LOG_TAG, "Expected tag matches: ${toNodeCountSummary(expectedTags)}")
        Log.e(LOG_TAG, "Merged semantics tree:\n${semanticsTreeDump(useUnmergedTree = false)}")
        Log.e(LOG_TAG, "Unmerged semantics tree:\n${semanticsTreeDump(useUnmergedTree = true)}")
    }

    private fun AndroidComposeTestRule<*, *>.toNodeCountSummary(expectedTags: List<String>): String {
        return expectedTags.joinToString { tag ->
            val mergedCount = nodeCountForTag(testTag = tag, useUnmergedTree = false)
            val unmergedCount = nodeCountForTag(testTag = tag, useUnmergedTree = true)
            "$tag(merged=$mergedCount, unmerged=$unmergedCount)"
        }
    }

    private fun AndroidComposeTestRule<*, *>.nodeCountForTag(testTag: String, useUnmergedTree: Boolean): String {
        return runCatching {
            onAllNodesWithTag(testTag = testTag, useUnmergedTree = useUnmergedTree)
                .fetchSemanticsNodes()
                .size
                .toString()
        }.getOrElse { throwable ->
            "unavailable(${throwable.javaClass.simpleName})"
        }
    }

    private fun AndroidComposeTestRule<*, *>.semanticsTreeDump(useUnmergedTree: Boolean): String {
        return runCatching {
            onRoot(useUnmergedTree = useUnmergedTree).printToString()
        }.getOrElse { throwable ->
            "unavailable(${throwable.javaClass.simpleName}: ${throwable.message})"
        }
    }

    private companion object {
        const val LOG_TAG = "JetHabitSmoke"

        val bottomNavigationRoutes = listOf(
            AppScreens.Daily.title,
            AppScreens.Health.title,
            AppScreens.Statistics.title,
            AppScreens.Chat.title,
            AppScreens.Profile.title
        )
    }
}
