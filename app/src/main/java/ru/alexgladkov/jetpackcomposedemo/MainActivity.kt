package ru.alexgladkov.jetpackcomposedemo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.alexgladkov.jetpackcomposedemo.screens.daily.DailyScreen
import ru.alexgladkov.jetpackcomposedemo.screens.settings.SettingsScreen
import ru.alexgladkov.jetpackcomposedemo.ui.theme.JetpackComposeDemoTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitStyle
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.MainTheme

class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkModeValue = isSystemInDarkTheme()

            val currentStyle = remember { mutableStateOf(JetHabbitStyle.Purple) }
            val isDarkMode = remember { mutableStateOf(isDarkModeValue) }

            MainTheme(
                style = currentStyle.value,
                darkTheme = isDarkMode.value
            ) {
                val navController = rememberNavController()

                Surface(color = JetHabbitTheme.colors.primaryBackground) {
                    NavHost(navController = navController, startDestination = "daily") {
                        composable("daily") { DailyScreen(navController = navController) }
                        composable("settings") {
                            SettingsScreen(
                                onNewStyle = {
                                    currentStyle.value = it
                                },
                                onDarkModeChanged = {
                                    isDarkMode.value = it
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
    JetpackComposeDemoTheme {
        DailyScreen(navController = rememberNavController())
    }
}