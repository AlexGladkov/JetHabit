import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import data.features.settings.SettingsEventBus
import di.LocalPlatform
import di.Platform
import di.PlatformConfiguration
import di.PlatformSDK
import platform.UIKit.UIViewController
import themes.MainTheme
import coil3.PlatformContext
import core.database.getDatabaseBuilder
import core.database.getRoomDatabase
import core.di.initializeCoil

fun MainViewController(): UIViewController {
    val appDatabase = getRoomDatabase(getDatabaseBuilder())
    PlatformSDK.init(PlatformConfiguration(), appDatabase = appDatabase)

    return ComposeUIViewController {
        val settingsEventBus = remember { SettingsEventBus() }
        val currentSettings = settingsEventBus.currentSettings.collectAsState().value

        MainTheme(
            style = currentSettings.style,
            darkTheme = currentSettings.isDarkMode,
            corners = currentSettings.cornerStyle,
            textSize = currentSettings.textSize,
            paddingSize = currentSettings.paddingSize
        ) {
            CompositionLocalProvider(LocalPlatform provides Platform.iOS) {
                App()
            }
        }
    }
}

fun initializeIOSApp() {
    initializeCoil(PlatformContext.INSTANCE)
}
