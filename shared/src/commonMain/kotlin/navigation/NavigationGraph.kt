package navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.alexgladkov.odyssey.compose.extensions.bottomNavigation
import ru.alexgladkov.odyssey.compose.extensions.screen
import ru.alexgladkov.odyssey.compose.extensions.tab
import ru.alexgladkov.odyssey.compose.navigation.RootComposeBuilder
import ru.alexgladkov.odyssey.compose.navigation.bottom_bar_navigation.BottomNavConfiguration
import ru.alexgladkov.odyssey.compose.navigation.bottom_bar_navigation.TabConfiguration
import ru.alexgladkov.odyssey.compose.navigation.bottom_bar_navigation.TabItem
import ru.alexgladkov.odyssey.compose.navigation.bottom_bar_navigation.TabsNavModel
import screens.compose.ComposeScreen
import screens.daily.DailyScreen
import screens.daily.views.HabitCardItemModel
import screens.detail.DetailScreen
import screens.settings.SettingsScreen
import screens.splash.SplashScreen
import ui.themes.JetHabitTheme

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
fun RootComposeBuilder.navigationGraph(
    backgroundColor: Color,
    selectedColor: Color,
    unselectedColor: Color,
    dailyIcon: Painter,
    settingsIcon: Painter
) {
    screen("splash") {
        SplashScreen()
    }

    bottomNavigation(
        "main",
        tabsNavModel = BottomConfiguration(backgroundColor, selectedColor, unselectedColor)
    ) {
        tab(DailyTab(selectedColor, unselectedColor, dailyIcon)) {
            screen("daily") {
                DailyScreen()
            }

            screen("detail") {
                DetailScreen(it as HabitCardItemModel)
            }
        }

        tab(SettingsTab(selectedColor, unselectedColor, settingsIcon)) {
            screen("settings") {
                SettingsScreen()
            }
        }
    }

    screen("compose") {
        ComposeScreen()
    }
}

class BottomConfiguration(
    private val backgroundColor: Color,
    private val selectedColor: Color, private val unselectedColor: Color
) : TabsNavModel<BottomNavConfiguration>() {

    override val navConfiguration: BottomNavConfiguration
        get() {
            return BottomNavConfiguration(
                backgroundColor = backgroundColor,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                elevation = 0.dp
            )
        }
}

class DailyTab(
    private val selectedColor: Color, private val unselectedColor: Color,
    private val icon: Painter
) : TabItem() {
    override val configuration: TabConfiguration
        get() {
            return TabConfiguration(
                title = "Daily",
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                selectedIcon = icon,
                unselectedIcon = icon,
                titleStyle = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
}

class SettingsTab(
    private val selectedColor: Color, private val unselectedColor: Color,
    private val icon: Painter
) : TabItem() {
    override val configuration: TabConfiguration
        get() {
            return TabConfiguration(
                title = "Settings",
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                selectedIcon = icon,
                unselectedIcon = icon,
                titleStyle = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
}