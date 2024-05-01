import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import data.features.settings.SettingsEventBus
import navigation.LocalNavHost
import screens.create.CreateHabitFlow
import screens.main.MainScreen
import screens.splash.SplashScreen
import ui.themes.MainTheme

@Composable
fun App() {
    val settingsEventBus = remember { SettingsEventBus() }
    val currentSettings = settingsEventBus.currentSettings.collectAsState().value

    MainTheme(
        style = currentSettings.style,
        darkTheme = currentSettings.isDarkMode,
        corners = currentSettings.cornerStyle,
        textSize = currentSettings.textSize,
        paddingSize = currentSettings.paddingSize
    ) {
        JetHabitApp()
    }
}

enum class NavigationScreens(val title: String) {
    Splash("splash"), Main("main"), Create("create")
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun JetHabitApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route ?: NavigationScreens.Splash.title

    CompositionLocalProvider(
        LocalNavHost provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = NavigationScreens.Splash.title
        ) {
            composable(route = NavigationScreens.Splash.title) {
                SplashScreen(navController)
            }

            composable(route = NavigationScreens.Main.title) {
                MainScreen()
            }

            composable(route = NavigationScreens.Create.title) {
                CreateHabitFlow()
            }
        }
    }
}