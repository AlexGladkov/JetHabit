package tech.mobiledeveloper.jethabit.app

import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import androidx.test.ext.junit.runners.AndroidJUnit4
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
