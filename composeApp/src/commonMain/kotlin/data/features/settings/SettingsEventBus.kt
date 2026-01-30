package data.features.settings

import androidx.compose.runtime.staticCompositionLocalOf
import domain.SettingsBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ui.themes.JetHabitCorners
import ui.themes.JetHabitSize
import ui.themes.JetHabitStyle

class SettingsEventBus(
    private val repository: UserSettingsRepository? = null
) {

    private val scope = CoroutineScope(Dispatchers.Main)

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

    init {
        // Load settings from database if repository is available
        repository?.let {
            scope.launch {
                val savedSettings = it.getSettings()
                if (savedSettings != null) {
                    _currentSettings.value = savedSettings
                }
            }
        }
    }

    fun updateDarkMode(isDarkMode: Boolean) {
        println("New value $isDarkMode")
        _currentSettings.value = _currentSettings.value.copy(isDarkMode = isDarkMode)
        persistSettings()
    }

    fun updateCornerStyle(corners: JetHabitCorners) {
        _currentSettings.value = _currentSettings.value.copy(cornerStyle = corners)
        persistSettings()
    }

    fun updateStyle(style: JetHabitStyle) {
        _currentSettings.value = _currentSettings.value.copy(style = style)
        persistSettings()
    }

    fun updateTextSize(textSize: JetHabitSize) {
        _currentSettings.value = _currentSettings.value.copy(textSize = textSize)
        persistSettings()
    }

    fun updatePaddingSize(paddingSize: JetHabitSize) {
        _currentSettings.value = _currentSettings.value.copy(paddingSize = paddingSize)
        persistSettings()
    }

    private fun persistSettings() {
        repository?.let {
            scope.launch {
                it.saveSettings(_currentSettings.value)
            }
        }
    }
}

internal val LocalSettingsEventBus = staticCompositionLocalOf {
    SettingsEventBus()
}