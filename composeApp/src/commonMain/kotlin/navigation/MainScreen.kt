package navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import feature.daily.ui.DailyScreen
import feature.detail.ui.DetailScreen
import feature.health.list.ui.HealthScreen
import feature.health.track.ui.TrackHabitScreen
import feature.chat.ui.ChatScreen
import screens.settings.SettingsScreen
import feature.statistics.ui.StatisticsScreen
import feature.create.ui.ComposeScreen
import feature.profile.edit.ui.EditProfileScreen
import feature.profile.start.ui.ProfileScreen
import ui.themes.JetHabitTheme
import org.jetbrains.compose.resources.stringResource

enum class DailyScreens {
    Start, Detail
}

enum class HealthScreens {
    Start, Track, Create
}

enum class ChatScreens {
    Start
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        AppScreens.Daily,
        AppScreens.Health,
        AppScreens.Statistics,
        AppScreens.Chat,
        AppScreens.Profile
    )

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController,
            modifier = Modifier.padding(bottom = 56.dp).fillMaxSize(),
            startDestination = AppScreens.Daily.title
        ) {
            navigation(startDestination = DailyScreens.Start.name, route = AppScreens.Daily.title) {
                composable(DailyScreens.Start.name) { DailyScreen(navController) }
                composable("${DailyScreens.Detail.name}/{habitId}") { backStackEntry ->
                    val habitId = backStackEntry.arguments?.getString("habitId").orEmpty()
                    DetailScreen(habitId = habitId, navController = navController)
                }
            }
            navigation(startDestination = HealthScreens.Start.name, route = AppScreens.Health.title) {
                composable(HealthScreens.Start.name) { HealthScreen(navController) }
                composable("${HealthScreens.Track.name}/{habitId}") { backStackEntry ->
                    val habitId = backStackEntry.arguments?.getString("habitId").orEmpty()
                    TrackHabitScreen(
                        habitId = habitId,
                        navController = navController
                    )
                }
                composable("${HealthScreens.Create.name}?type={type}") { backStackEntry ->
                    val type = backStackEntry.arguments?.getString("type")
                    ComposeScreen(type = type)
                }
            }
            composable(AppScreens.Statistics.title) {
                StatisticsScreen()
            }
            composable(AppScreens.Chat.title) {
                ChatScreen(navController)
            }
            navigation(
                startDestination = ProfileScreens.Start.name,
                route = AppScreens.Profile.title
            ) {
                composable(ProfileScreens.Start.name) {
                    ProfileScreen(navController)
                }
                composable(ProfileScreens.Settings.name) {
                    SettingsScreen(navController)
                }
                composable(ProfileScreens.Edit.name) {
                    EditProfileScreen(navController)
                }
            }
        }

        BottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .testTag("BottomNavigation"),
            backgroundColor = JetHabitTheme.colors.secondaryBackground
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            items.forEach { screen ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            screen.icon,
                            tint = if (currentDestination?.hierarchy?.any { it.route == screen.title } == true)
                                JetHabitTheme.colors.tintColor
                            else
                                JetHabitTheme.colors.primaryText,
                            contentDescription = null
                        )
                    },
                    label = { 
                        Text(
                            stringResource(screen.titleRes), 
                            color = if (currentDestination?.hierarchy?.any { it.route == screen.title } == true)
                                JetHabitTheme.colors.tintColor
                            else
                                JetHabitTheme.colors.primaryText
                        ) 
                    },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.title } == true,
                    onClick = {
                        navController.navigate(screen.title) {
//                            popUpTo(navController.graph.findStartDestination().id) {
//                                saveState = true
//                            }
//                            launchSingleTop = true
//                            restoreState = true
                        }
                    },
                    selectedContentColor = JetHabitTheme.colors.tintColor,
                    unselectedContentColor = JetHabitTheme.colors.primaryText,
                    alwaysShowLabel = true
                )
            }
        }
    }
}