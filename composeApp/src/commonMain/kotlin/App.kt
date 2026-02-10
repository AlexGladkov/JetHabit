import androidx.compose.runtime.*
import data.features.settings.LocalSettingsEventBus
import data.features.settings.SettingsEventBus
import root.RootComponent
import root.RootContent
import themes.MainTheme

@Composable
fun App(rootComponent: RootComponent) {
    val settingsEventBus = remember { SettingsEventBus() }
    val currentSettings by settingsEventBus.currentSettings.collectAsState()

    MainTheme(
        style = currentSettings.style,
        darkTheme = currentSettings.isDarkMode,
        corners = currentSettings.cornerStyle,
        textSize = currentSettings.textSize,
        paddingSize = currentSettings.paddingSize
    ) {
        CompositionLocalProvider(
            LocalSettingsEventBus provides settingsEventBus
        ) {
            RootContent(rootComponent)
        }
    }
}

@Composable
fun PreviewApp(content: @Composable () -> Unit) {
    val settingsEventBus = remember { SettingsEventBus() }
    val currentSettings by settingsEventBus.currentSettings.collectAsState()

    MainTheme(
        style = currentSettings.style,
        darkTheme = currentSettings.isDarkMode,
        corners = currentSettings.cornerStyle,
        textSize = currentSettings.textSize,
        paddingSize = currentSettings.paddingSize
    ) {
        CompositionLocalProvider(
            LocalSettingsEventBus provides settingsEventBus,
            content = content
        )
    }
}