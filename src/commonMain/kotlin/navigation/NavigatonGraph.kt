package navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitTheme
import ru.alexgladkov.odyssey.compose.extensions.bottomNavigation
import screens.splash.SplashScreen
import ru.alexgladkov.odyssey.compose.extensions.screen
import ru.alexgladkov.odyssey.compose.extensions.tab
import ru.alexgladkov.odyssey.compose.navigation.RootComposeBuilder
import ru.alexgladkov.odyssey.compose.navigation.bottom_bar_navigation.BottomNavConfiguration
import ru.alexgladkov.odyssey.compose.navigation.bottom_bar_navigation.TabConfiguration
import ru.alexgladkov.odyssey.compose.navigation.bottom_bar_navigation.TabItem
import ru.alexgladkov.odyssey.compose.navigation.bottom_bar_navigation.TabsNavModel
import screens.compose.ComposeScreen
import screens.daily.DailyScreen
import screens.settings.SettingsScreen

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
fun RootComposeBuilder.navigationGraph() {
    screen("splash") {
        SplashScreen()
    }

    bottomNavigation("main", tabsNavModel = BottomConfiguration()) {
        tab(DailyTab()) {
            screen("daily") {
                DailyScreen()
            }
        }

        tab(SettingsTab()) {
            screen("settings") {
                SettingsScreen()
            }
        }
    }

    screen("compose") {
        ComposeScreen()
    }
}

class BottomConfiguration : TabsNavModel<BottomNavConfiguration>() {

    override val navConfiguration: BottomNavConfiguration
        @Composable
        get() {
            return BottomNavConfiguration(
                backgroundColor = JetHabitTheme.colors.primaryBackground,
                selectedColor = JetHabitTheme.colors.primaryText,
                unselectedColor = JetHabitTheme.colors.controlColor,
                elevation = 0.dp
            )
        }
}

class DailyTab : TabItem() {

    override val configuration: TabConfiguration
        @Composable
        get() {
            return TabConfiguration(
                title = "Daily",
                selectedColor = JetHabitTheme.colors.primaryText,
                unselectedColor = JetHabitTheme.colors.controlColor,
                titleStyle = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
}

class SettingsTab : TabItem() {
    override val configuration: TabConfiguration
        @Composable
        get() {
            return TabConfiguration(
                title = "Settings",
                selectedColor = JetHabitTheme.colors.primaryText,
                unselectedColor = JetHabitTheme.colors.controlColor,
                titleStyle = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
}