package navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import ru.alexgladkov.odyssey.compose.extensions.bottomNavigation
import ru.alexgladkov.odyssey.compose.extensions.flow
import ru.alexgladkov.odyssey.compose.extensions.screen
import ru.alexgladkov.odyssey.compose.extensions.tab
import ru.alexgladkov.odyssey.compose.navigation.RootComposeBuilder
import screens.add_dates.MedicationAddDates
import screens.add_name.MedicationAddName
import screens.compose.ComposeScreen
import screens.daily.DailyScreen
import screens.daily.views.HabitCardItemModel
import screens.detail.DetailScreen
import screens.settings.SettingsScreen
import screens.splash.SplashScreen
import ui.themes.JetHabitTheme

enum class NavigationScreens(val title: String) {
    Splash("splash"), Main("main"), Create("create")
}

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class
)
internal fun RootComposeBuilder.navigationGraph() {

//    bottomNavigation(
//        "main",
//        colors = BottomBarDefaults.bottomColors(
//            backgroundColor = JetHabitTheme.colors.primaryBackground
//        )
//    ) {
//        val colors = TabDefaults.simpleTabColors(
//            selectedColor = JetHabitTheme.colors.primaryText,
//            unselectedColor = JetHabitTheme.colors.controlColor
//        )
//
//        tab(TabDefaults.content(AppRes.string.title_daily, Icons.Filled.DateRange), colors) {
//            screen("daily") {
//                DailyScreen()
//            }
//
//            screen("detail") {
//                DetailScreen(it as HabitCardItemModel)
//            }
//        }
//
//        tab(TabDefaults.content(AppRes.string.title_statistics, Icons.Filled.Star), colors) {
//            screen("statistics") {
//                StatisticsScreen()
//            }
//        }
//
//        tab(TabDefaults.content(AppRes.string.title_settings, Icons.Filled.Settings), colors) {
//            screen("settings") {
//                SettingsScreen()
//            }
//        }
//    }

    screen("compose") {
        ComposeScreen()
    }

    medicationAddFlow()
}

internal fun RootComposeBuilder.medicationAddFlow() {
    flow("medication_add_flow") {
        screen("medication_name") {
            MedicationAddName()
        }

        screen("medication_add_dates") {
            MedicationAddDates(title = it as String)
        }
    }
}