package ru.alexgladkov.jetpackcomposedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.toArgb
import di.PlatformConfiguration
import di.PlatformSDK
import domain.LocalSettingsBundle
import domain.SettingsBundle
import navigation.navigationGraph
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitCorners
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitSize
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitStyle
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitTheme
import ru.alexgladkov.odyssey.compose.setup.OdysseyConfiguration
import ru.alexgladkov.odyssey.compose.setup.setNavigationContent
import ru.alexgladkov.odyssey.core.configuration.DisplayType
import ui.themes.MainTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlatformSDK.init(PlatformConfiguration(this))

        setContent {
            MainTheme(
                style = JetHabitStyle.Green,
                darkTheme = true
            ) {
                val configuration = OdysseyConfiguration(
                    canvas = this,
                    displayType = DisplayType.FullScreen,
                    statusBarColor = JetHabitTheme.colors.primaryBackground.toArgb(),
                    backgroundColor = JetHabitTheme.colors.primaryBackground
                )

                val settingsBundle = SettingsBundle(
                    isDarkMode = true, cornerStyle = JetHabitCorners.Flat,
                    style = JetHabitStyle.Blue, paddingSize = JetHabitSize.Medium, textSize = JetHabitSize.Medium
                )

                CompositionLocalProvider(
                    LocalSettingsBundle provides settingsBundle
                ) {
                    setNavigationContent(configuration) {
                        navigationGraph()
                    }
                }
            }
        }
    }
}