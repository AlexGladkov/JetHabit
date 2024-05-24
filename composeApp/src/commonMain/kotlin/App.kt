import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import data.features.settings.LocalSettingsEventBus
import data.features.settings.SettingsEventBus
import navigation.LocalNavHost
import screens.create.CreateHabitFlow
import screens.main.MainScreen
import screens.splash.SplashScreen
import themes.MainTheme

@Composable
fun App() {
    val settingsEventBus = remember { SettingsEventBus() }
    val currentSettings by settingsEventBus.currentSettings.collectAsState()

    MainTheme(
        style = currentSettings.style,
        darkTheme = currentSettings.isDarkMode,
        corners = currentSettings.cornerStyle,
        textSize = currentSettings.textSize,
        paddingSize = currentSettings.paddingSize
    ) {
        CompositionLocalProvider(
            LocalSettingsEventBus provides settingsEventBus
        ) {
            JetHabitApp()
        }
    }
}

@Composable
fun PreviewApp(content: @Composable () -> Unit) {
    val settingsEventBus = remember { SettingsEventBus() }
    val currentSettings by settingsEventBus.currentSettings.collectAsState()

    MainTheme(
        style = currentSettings.style,
        darkTheme = currentSettings.isDarkMode,
        corners = currentSettings.cornerStyle,
        textSize = currentSettings.textSize,
        paddingSize = currentSettings.paddingSize
    ) {
        CompositionLocalProvider(
            LocalSettingsEventBus provides settingsEventBus,
            content = content
        ) 
    }
}

enum class NavigationScreens(val title: String) {
    Splash("splash"), Main("main"), Create("create")
}

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