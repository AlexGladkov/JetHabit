package ru.alexgladkov.jetpackcomposedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import ru.alexgladkov.jetpackcomposedemo.ui.themes.MainTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.MainThemeDefaultSettings
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

            var jetHabitThemeSettings by remember { mutableStateOf(MainThemeDefaultSettings.copy(
                isDarkMode = isDarkModeValue
            )) }

            MainTheme(
                jetHabitThemeSettings = jetHabitThemeSettings,
            ) {
                val navController = rememberNavController()
                val systemUiController = rememberSystemUiController()

                // Set status bar color
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = if (jetHabitThemeSettings.isDarkMode) baseDarkPalette.primaryBackground else baseLightPalette.primaryBackground,
                        darkIcons = !jetHabitThemeSettings.isDarkMode
                    )
                }

                Surface {
                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            SplashScreen(navController = navController)
                        }

                        composable("main") {
                            val settings = SettingsBundle(
                                isDarkMode = jetHabitThemeSettings.isDarkMode,
                                style = jetHabitThemeSettings.style,
                                textSize = jetHabitThemeSettings.textSize,
                                cornerStyle = jetHabitThemeSettings.corners,
                                paddingSize = jetHabitThemeSettings.paddingSize
                            )

                            MainScreen(navController = navController,
                                settings = settings, onSettingsChanged = {
                                    jetHabitThemeSettings = jetHabitThemeSettings.copy(
                                        isDarkMode = it.isDarkMode,
                                        style = it.style,
                                        textSize = it.textSize,
                                        corners = it.cornerStyle,
                                        paddingSize = it.paddingSize
                                    )
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