package navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import feature.daily.ui.DailyScreen
import feature.detail.ui.DetailScreen
import feature.health.ui.HealthScreen
import screens.settings.SettingsScreen
import screens.stats.StatisticsScreen
import ui.themes.JetHabitTheme
import org.jetbrains.compose.resources.stringResource

enum class DailyScreens {
    Start, Detail
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        AppScreens.Daily,
        AppScreens.Health,
        AppScreens.Statistics,
        AppScreens.Settings
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
            composable(AppScreens.Health.title) { HealthScreen() }
            composable(AppScreens.Statistics.title) { StatisticsScreen() }
            composable(AppScreens.Settings.title) { SettingsScreen() }
        }

        BottomNavigation(
            modifier = Modifier.align(Alignment.BottomStart),
            backgroundColor = JetHabitTheme.colors.secondaryBackground
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            items.forEach { screen ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            screen.icon,
                            tint = JetHabitTheme.colors.primaryText,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(screen.titleRes), color = JetHabitTheme.colors.primaryText) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.title } == true,
                    onClick = {
                        navController.navigate(screen.title) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}