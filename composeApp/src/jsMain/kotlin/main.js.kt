import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.features.settings.LocalSettingsEventBus
import data.features.settings.SettingsEventBus
import di.LocalPlatform
import di.Platform
import di.PlatformConfiguration
import di.PlatformSDK
import ui.themes.JetHabitTheme
import themes.MainTheme

@Composable
fun MainView() {
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
        val backgroundColor = JetHabitTheme.colors.primaryBackground
        CompositionLocalProvider(
            LocalPlatform provides Platform.Js,
            LocalSettingsEventBus provides settingsEventBus
        ) {
            Column {
                Box(modifier = Modifier.fillMaxWidth().height(30.dp).background(backgroundColor))
                App()
                Box(modifier = Modifier.fillMaxWidth().height(30.dp).background(backgroundColor))
            }
        }
    }
}

