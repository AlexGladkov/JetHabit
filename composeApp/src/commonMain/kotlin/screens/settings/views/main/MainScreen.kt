package screens.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import feature.daily.ui.DailyScreen
import screens.settings.SettingsScreen
import screens.stats.StatisticsScreen
import ui.themes.JetHabitTheme

sealed class MainScreens(val route: String, val title: String, val image: ImageVector) {
    data object Daily : MainScreens("daily", "Daily", Icons.AutoMirrored.Filled.List)
    data object Statistics : MainScreens("statistics", "Statistics", Icons.Outlined.Check)
    data object Settings : MainScreens("settings", "Settings", Icons.Outlined.Settings)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(MainScreens.Daily, MainScreens.Statistics, MainScreens.Settings)

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController, modifier = Modifier.padding(bottom = 56.dp).fillMaxSize(), startDestination = MainScreens.Daily.route) {
            composable(MainScreens.Daily.route) { DailyScreen() }
            composable(MainScreens.Statistics.route) { StatisticsScreen() }
            composable(MainScreens.Settings.route) { SettingsScreen() }
        }
        
        BottomNavigation(modifier = Modifier.align(Alignment.BottomStart), backgroundColor = JetHabitTheme.colors.secondaryBackground) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
        
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.image, tint = JetHabitTheme.colors.primaryText, contentDescription = screen.title) },
                        label = { Text(screen.title, color = JetHabitTheme.colors.primaryText) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to  the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().displayName) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }   
    }
}