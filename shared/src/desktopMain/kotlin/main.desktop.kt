import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import data.features.settings.LocalSettingsEventBus
import data.features.settings.SettingsEventBus
import di.LocalPlatform
import di.Platform
import di.PlatformConfiguration
import di.PlatformSDK
import navigation.navigationGraph
import ru.alexgladkov.odyssey.compose.setup.OdysseyConfiguration
import ru.alexgladkov.odyssey.compose.setup.setNavigationContent
import ui.themes.JetHabitTheme
import ui.themes.MainTheme

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
        val odysseyConfiguration = OdysseyConfiguration(
            backgroundColor = JetHabitTheme.colors.primaryBackground
        )

        CompositionLocalProvider(
            LocalPlatform provides Platform.Desktop,
            LocalSettingsEventBus provides settingsEventBus
        ) {
            setNavigationContent(odysseyConfiguration) {
                navigationGraph()
            }

        }
    }
}