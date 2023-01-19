package screens.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import domain.SettingsBundle

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun MainScreen(
    settings: SettingsBundle,
    onSettingsChanged: (SettingsBundle) -> Unit
) {
//    val childNavController = rememberNavController()
//
//    // Navigation Items
//    val items = listOf(
//        MainBottomScreen.Daily,
//        MainBottomScreen.Settings,
//    )
//
//    Column {
//        Box(modifier = Modifier.weight(1f)) {
//            NavHost(
//                navController = childNavController,
//                startDestination = MainBottomScreen.Daily.route
//            ) {
//                dailyFlow(navController)
//
//                composable(MainBottomScreen.Settings.route) {
//                    SettingsScreen(settings = settings, onSettingsChanged = onSettingsChanged)
//                }
//            }
//        }
//
//        Box(
//            modifier = Modifier
//                .height(56.dp)
//                .fillMaxWidth()
//        ) {
//            BottomNavigation {
//                val navBackStackEntry by childNavController.currentBackStackEntryAsState()
//                val currentDestination = navBackStackEntry?.destination
//                val previousDestination = remember { mutableStateOf(items.first().route) }
//
//                items.forEach { screen ->
//                    val isSelected = currentDestination?.hierarchy
//                        ?.any { it.route == screen.route } == true
//
//                    BottomNavigationItem(
//                        modifier = Modifier.background(JetHabitTheme.colors.primaryBackground),
//                        icon = {
//                            Icon(
//                                imageVector = when (screen) {
//                                    MainBottomScreen.Daily -> Icons.Filled.Favorite
//                                    MainBottomScreen.Settings -> Icons.Filled.Settings
//                                },
//                                contentDescription = null,
//                                tint = if (isSelected) JetHabitTheme.colors.tintColor else JetHabitTheme.colors.controlColor
//                            )
//                        },
//                        label = {
//                            Text(
//                                stringResource(id = screen.resourceId),
//                                color = if (isSelected) JetHabitTheme.colors.primaryText else JetHabitTheme.colors.controlColor
//                            )
//                        },
//                        selected = isSelected,
//                        onClick = {
//                            if (screen.route == previousDestination.value) return@BottomNavigationItem
//                            previousDestination.value = screen.route
//
//                            childNavController.navigate(screen.route) {
//                                popUpTo(childNavController.graph.findStartDestination().id) {
//                                    saveState = true
//                                }
//
//                                launchSingleTop = true
//                                restoreState = true
//                            }
//                        })
//                }
//            }
//        }
//    }
}