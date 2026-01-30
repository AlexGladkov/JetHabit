package data.features.settings

import core.database.dao.UserSettingsDao
import core.database.entity.UserSettings
import domain.SettingsBundle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ui.themes.JetHabitCorners
import ui.themes.JetHabitSize
import ui.themes.JetHabitStyle

class UserSettingsRepository(
    private val userSettingsDao: UserSettingsDao
) {
    fun observeSettings(): Flow<SettingsBundle?> {
        return userSettingsDao.getUserSettings().map { entity ->
            entity?.toSettingsBundle()
        }
    }

    suspend fun getSettings(): SettingsBundle? {
        return userSettingsDao.getUserSettingsOnce()?.toSettingsBundle()
    }

    suspend fun saveSettings(settings: SettingsBundle) {
        userSettingsDao.insertOrUpdateSettings(settings.toEntity())
    }

    private fun UserSettings.toSettingsBundle(): SettingsBundle {
        return SettingsBundle(
            isDarkMode = isDarkMode,
            style = runCatching { JetHabitStyle.valueOf(style) }.getOrDefault(JetHabitStyle.Orange),
            textSize = runCatching { JetHabitSize.valueOf(textSize) }.getOrDefault(JetHabitSize.Medium),
            paddingSize = runCatching { JetHabitSize.valueOf(paddingSize) }.getOrDefault(JetHabitSize.Medium),
            cornerStyle = runCatching { JetHabitCorners.valueOf(cornerStyle) }.getOrDefault(JetHabitCorners.Rounded)
        )
    }

    private fun SettingsBundle.toEntity(): UserSettings {
        return UserSettings(
            id = 1,
            isDarkMode = isDarkMode,
            style = style.name,
            textSize = textSize.name,
            paddingSize = paddingSize.name,
            cornerStyle = cornerStyle.name
        )
    }
}
