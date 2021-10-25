package ru.alexgladkov.jetpackcomposedemo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import ru.alexgladkov.jetpackcomposedemo.screens.main.MainBottomScreen
import ru.alexgladkov.jetpackcomposedemo.screens.settings.SettingsScreen
import ru.alexgladkov.jetpackcomposedemo.screens.tabs.dailyFlow
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitCorners
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitSize
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitStyle
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.MainTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.baseDarkPalette
import ru.alexgladkov.jetpackcomposedemo.ui.themes.baseLightPalette

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkModeValue = true // isSystemInDarkTheme()

            val currentStyle = remember { mutableStateOf(JetHabitStyle.Purple) }
            val currentFontSize = remember { mutableStateOf(JetHabitSize.Medium) }
            val currentPaddingSize = remember { mutableStateOf(JetHabitSize.Medium) }
            val currentCornersStyle = remember { mutableStateOf(JetHabitCorners.Rounded) }
            val isDarkMode = remember { mutableStateOf(isDarkModeValue) }

            MainTheme(
                style = currentStyle.value,
                darkTheme = isDarkMode.value,
                textSize = currentFontSize.value,
                corners = currentCornersStyle.value,
                paddingSize = currentPaddingSize.value
            ) {
                val navController = rememberNavController()
                val systemUiController = rememberSystemUiController()

                // Set status bar color
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = if (isDarkMode.value) baseDarkPalette.primaryBackground else baseLightPalette.primaryBackground,
                        darkIcons = !isDarkMode.value
                    )
                }

                // Navigation Items
                val items = listOf(
                    MainBottomScreen.Daily,
                    MainBottomScreen.Settings,
                )

                Surface {
                    Column {
                        Box(modifier = Modifier.weight(1f)) {
                            NavHost(
                                navController = navController,
                                startDestination = MainBottomScreen.Daily.route
                            ) {
                                dailyFlow(navController)

                                composable(MainBottomScreen.Settings.route) {
                                        SettingsScreen(
                                            isDarkMode = isDarkMode.value,
                                            currentTextSize = currentFontSize.value,
                                            currentPaddingSize = currentPaddingSize.value,
                                            currentCornersStyle = currentCornersStyle.value,
                                            onDarkModeChanged = {
                                                isDarkMode.value = it
                                            },
                                            onNewStyle = {
                                                currentStyle.value = it
                                            },
                                            onTextSizeChanged = {
                                                currentFontSize.value = it
                                            },
                                            onCornersStyleChanged = {
                                                currentCornersStyle.value = it
                                            },
                                            onPaddingSizeChanged = {
                                                currentPaddingSize.value = it
                                            }
                                        )
                                    }
                                }
                        }

                        Box(modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth()) {
                            BottomNavigation {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentDestination = navBackStackEntry?.destination
                                val previousDestination = remember { mutableStateOf(items.first().route) }

                                items.forEach { screen ->
                                    val isSelected = currentDestination?.hierarchy
                                        ?.any { it.route == screen.route } == true

                                    BottomNavigationItem(
                                        modifier = Modifier.background(JetHabitTheme.colors.primaryBackground),
                                        icon = {
                                            Icon(
                                                imageVector = when (screen) {
                                                    MainBottomScreen.Daily -> Icons.Filled.Favorite
                                                    MainBottomScreen.Settings -> Icons.Filled.Settings
                                                },
                                                contentDescription = null,
                                                tint = if (isSelected) JetHabitTheme.colors.tintColor else JetHabitTheme.colors.controlColor
                                            )
                                        },
                                        label = {
                                            Text(
                                                stringResource(id = screen.resourceId),
                                                color = if (isSelected) JetHabitTheme.colors.primaryText else JetHabitTheme.colors.controlColor
                                            )
                                        },
                                        selected = isSelected,
                                        onClick = {
                                            if (screen.route == previousDestination.value) return@BottomNavigationItem
                                            previousDestination.value = screen.route

                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }

                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}