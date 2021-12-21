package ru.alexgladkov.jetpackcomposedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import ru.alexgladkov.jetpackcomposedemo.domain.SettingsBundle
import ru.alexgladkov.jetpackcomposedemo.screens.compose.ComposeScreen
import ru.alexgladkov.jetpackcomposedemo.screens.compose.ComposeViewModel
import ru.alexgladkov.jetpackcomposedemo.screens.main.MainScreen
import ru.alexgladkov.jetpackcomposedemo.screens.splash.SplashScreen
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitCorners
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitSize
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitStyle
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