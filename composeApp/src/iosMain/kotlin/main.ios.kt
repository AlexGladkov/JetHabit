import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import data.features.settings.SettingsEventBus
import di.PlatformConfiguration
import di.PlatformSDK
import platform.UIKit.UIViewController
import themes.MainTheme
import coil3.PlatformContext
import coil3.SingletonPlatformContext
import core.di.initializeCoil

fun MainViewController(): UIViewController =
    ComposeUIViewController {
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
            App()
        }
    }

fun initializeIOSApp() {
    initializeCoil(SingletonPlatformContext)
}
