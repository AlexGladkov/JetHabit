package ru.alexgladkov.jetpackcomposedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import data.features.settings.LocalSettingsEventBus
import data.features.settings.SettingsEventBus
import di.PlatformConfiguration
import di.PlatformSDK
import navigation.navigationGraph
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitStyle
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.components.JetHabitButton
import ru.alexgladkov.odyssey.compose.setup.OdysseyConfiguration
import ru.alexgladkov.odyssey.compose.setup.setNavigationContent
import ru.alexgladkov.odyssey.core.configuration.DisplayType
import ui.themes.MainTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlatformSDK.init(PlatformConfiguration(this))

        setContent {
            val settingsEventBus = remember { SettingsEventBus() }
            val currentSettings = settingsEventBus.currentSettings.collectAsState().value

            MainTheme(
                style = currentSettings.style,
                darkTheme = currentSettings.isDarkMode,
                corners = currentSettings.cornerStyle,
                textSize = currentSettings.textSize,
                paddingSize = currentSettings.paddingSize
            ) {
                val configuration = OdysseyConfiguration(
                    canvas = this,
                    displayType = DisplayType.FullScreen,
                    statusBarColor = JetHabitTheme.colors.primaryBackground.toArgb(),
                    backgroundColor = JetHabitTheme.colors.primaryBackground
                )


                CompositionLocalProvider(
                    LocalSettingsEventBus provides settingsEventBus
                ) {
                    setNavigationContent(configuration) {
                        navigationGraph()
                    }
                }
            }
        }
    }
}

@Composable
fun Test() {
    val settingsEvent = LocalSettingsEventBus.current

    Box(modifier = Modifier.fillMaxSize()) {
        if (settingsEvent.currentSettings.value.style == JetHabitStyle.Red) {
            JetHabitButton(onClick = {
                settingsEvent.updateStyle(JetHabitStyle.Purple)
            })
        } else {
            JetHabitButton(onClick = {
                settingsEvent.updateStyle(JetHabitStyle.Red)
            })
        }
    }
}