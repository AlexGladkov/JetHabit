import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import coil3.PlatformContext
import core.database.getDatabaseBuilder
import core.database.getRoomDatabase
import data.features.settings.LocalSettingsEventBus
import data.features.settings.SettingsEventBus
import di.LocalPlatform
import di.Platform
import di.PlatformConfiguration
import di.PlatformSDK
import themes.MainTheme
import core.di.initializeCoil

fun main() {
    initializeCoil(PlatformContext.INSTANCE)

    application {
        val runtime = remember {
            DesktopRuntime(appDatabase = getRoomDatabase(getDatabaseBuilder())).also { it.bootstrap() }
        }
        DisposableEffect(runtime) {
            onDispose { runtime.close() }
        }
        Window(
            onCloseRequest = {
                runtime.close()
                exitApplication()
            },
            title = "JetHabit"
        ) {
            MainView(runtime)
        }
    }
}

@Composable
fun MainView(runtime: DesktopRuntime) {
    val currentSettings = runtime.settingsEventBus.currentSettings.collectAsState().value

    MainTheme(
        style = currentSettings.style,
        darkTheme = currentSettings.isDarkMode,
        corners = currentSettings.cornerStyle,
        textSize = currentSettings.textSize,
        paddingSize = currentSettings.paddingSize
    ) {
        CompositionLocalProvider(
            LocalPlatform provides runtime.platform,
            LocalSettingsEventBus provides runtime.settingsEventBus
        ) {
            App()
        }
    }
}