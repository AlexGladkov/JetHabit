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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import ru.alexgladkov.jetpackcomposedemo.domain.SettingsBundle
import ru.alexgladkov.jetpackcomposedemo.screens.compose.ComposeScreen
import ru.alexgladkov.jetpackcomposedemo.screens.compose.ComposeViewModel
import ru.alexgladkov.jetpackcomposedemo.screens.main.MainBottomScreen
import ru.alexgladkov.jetpackcomposedemo.screens.main.MainScreen
import ru.alexgladkov.jetpackcomposedemo.screens.settings.SettingsScreen
import ru.alexgladkov.jetpackcomposedemo.screens.splash.SplashScreen
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

    @OptIn(
        ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
        ExperimentalMaterialApi::class
    )
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

                Surface {
                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            SplashScreen(navController = navController)
                        }

                        composable("main") {
                            val settings = SettingsBundle(
                                isDarkMode = isDarkMode.value,
                                style = currentStyle.value,
                                textSize = currentFontSize.value,
                                cornerStyle = currentCornersStyle.value,
                                paddingSize = currentPaddingSize.value
                            )

                            MainScreen(navController = navController,
                                settings = settings, onSettingsChanged = {
                                    isDarkMode.value = it.isDarkMode
                                    currentStyle.value = it.style
                                    currentFontSize.value = it.textSize
                                    currentCornersStyle.value = it.cornerStyle
                                    currentPaddingSize.value = it.paddingSize
                                }
                            )
                        }

                        composable("compose") {
                            val composeViewModel = hiltViewModel<ComposeViewModel>()
                            ComposeScreen(navController = navController, composeViewModel = composeViewModel)
                        }
                    }
                }
            }
        }
    }
}