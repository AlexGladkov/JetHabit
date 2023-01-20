import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import data.features.settings.LocalSettingsEventBus
import data.features.settings.SettingsEventBus
import di.PlatformConfiguration
import di.PlatformSDK
import navigation.navigationGraph
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitTheme
import ru.alexgladkov.odyssey.compose.setup.OdysseyConfiguration
import ru.alexgladkov.odyssey.compose.setup.setNavigationContent
import ui.themes.MainTheme

fun main() = singleWindowApplication(
    title = "TeslaApp",
    state = WindowState(
        size = DpSize(800.dp, 600.dp),
        position = WindowPosition(Alignment.Center)
    )
) {
    PlatformSDK.init(PlatformConfiguration())
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