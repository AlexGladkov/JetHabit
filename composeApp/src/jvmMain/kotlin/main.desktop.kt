import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import coil3.SingletonPlatformContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import core.database.getDatabaseBuilder
import core.database.getRoomDatabase
import data.features.settings.LocalSettingsEventBus
import data.features.settings.SettingsEventBus
import di.LocalPlatform
import di.Platform
import di.PlatformConfiguration
import di.PlatformSDK
import root.RootComponent
import themes.MainTheme
import core.di.initializeCoil

fun main() {
    initializeCoil(SingletonPlatformContext)
    
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "JetHabit"
        ) {
            MainView()
        }
    }
}

@Composable
fun MainView() {
    val appDatabase = remember { getRoomDatabase(getDatabaseBuilder()) }
    PlatformSDK.init(PlatformConfiguration(), appDatabase = appDatabase)

    val lifecycle = remember { LifecycleRegistry() }
    val rootComponent = remember {
        RootComponent(
            componentContext = DefaultComponentContext(lifecycle),
            di = PlatformSDK.di
        )
    }

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
            App(rootComponent)
        }
    }
}