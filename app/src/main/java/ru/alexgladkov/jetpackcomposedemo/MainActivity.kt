package ru.alexgladkov.jetpackcomposedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.alexgladkov.jetpackcomposedemo.screens.daily.DailyScreen
import ru.alexgladkov.jetpackcomposedemo.screens.settings.SettingsScreen
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitCorners
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitSize
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitStyle
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.MainTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.baseDarkPalette
import ru.alexgladkov.jetpackcomposedemo.ui.themes.baseLightPalette

class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkModeValue = isSystemInDarkTheme()

            val currentStyle = remember { mutableStateOf(JetHabbitStyle.Purple) }
            val currentFontSize = remember { mutableStateOf(JetHabbitSize.Medium) }
            val currentPaddingSize = remember { mutableStateOf(JetHabbitSize.Medium) }
            val currentCornersStyle = remember { mutableStateOf(JetHabbitCorners.Rounded) }
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

                Surface(color = JetHabbitTheme.colors.primaryBackground) {
                    NavHost(navController = navController, startDestination = "daily") {
                        composable("daily") { DailyScreen(navController = navController) }
                        composable("settings") {
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
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainTheme(
        darkTheme = true
    ) {
        DailyScreen(navController = rememberNavController())
    }
}