import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import core.database.getDatabaseBuilder
import core.database.getRoomDatabase
import data.features.settings.LocalSettingsEventBus
import data.features.settings.SettingsEventBus
import di.LocalPlatform
import di.Platform
import di.PlatformConfiguration
import di.PlatformSDK
import themes.MainTheme

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "JetHabit") {
        MainView()
    }
}

@Composable
fun MainView() {
    val appDatabase = remember { getRoomDatabase(getDatabaseBuilder()) }
    PlatformSDK.init(PlatformConfiguration(), appDatabase = appDatabase)

    val settingsEventBus = remember { SettingsEventBus() }
    val currentSettings = settingsEventBus.currentSettings.collectAsState().value

    MainTheme(
        style = currentSettings.style,
        darkTheme = currentSettings.isDarkMode,
        corners = currentSettings.cornerStyle,
        textSize = currentSettings.textSize,
        paddingSize = currentSettings.paddingSize
    ) {
        CompositionLocalProvider(
            LocalPlatform provides Platform.Desktop,
            LocalSettingsEventBus provides settingsEventBus
        ) {
            App()
        }
    }
}