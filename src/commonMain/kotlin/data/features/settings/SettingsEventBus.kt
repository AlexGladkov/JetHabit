package data.features.settings

import androidx.compose.runtime.staticCompositionLocalOf
import domain.SettingsBundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitCorners
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitSize
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitStyle

class SettingsEventBus {

    private val _currentSettings: MutableStateFlow<SettingsBundle> = MutableStateFlow(
        SettingsBundle(
            isDarkMode = true,
            cornerStyle = JetHabitCorners.Rounded,
            style = JetHabitStyle.Orange,
            textSize = JetHabitSize.Medium,
            paddingSize = JetHabitSize.Medium
        )
    )
    val currentSettings: StateFlow<SettingsBundle> = _currentSettings

    fun updateDarkMode(isDarkMode: Boolean) {
        _currentSettings.value = _currentSettings.value.copy(isDarkMode = isDarkMode)
        println("DEBUG: ${_currentSettings.value}")
    }

    fun updateCornerStyle(corners: JetHabitCorners) {
        _currentSettings.value = _currentSettings.value.copy(cornerStyle = corners)
    }

    fun updateStyle(style: JetHabitStyle) {
        _currentSettings.value = _currentSettings.value.copy(style = style)
    }

    fun updateTextSize(textSize: JetHabitSize) {
        _currentSettings.value = _currentSettings.value.copy(textSize = textSize)
    }

    fun updatePaddingSize(paddingSize: JetHabitSize) {
        _currentSettings.value = _currentSettings.value.copy(paddingSize = paddingSize)
    }
}

val LocalSettingsEventBus = staticCompositionLocalOf {
    SettingsEventBus()
}