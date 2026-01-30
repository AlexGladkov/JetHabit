import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import data.features.settings.LocalSettingsEventBus
import data.features.settings.SettingsEventBus
import data.features.settings.UserSettingsRepository
import di.Inject
import navigation.LocalNavHost
import screens.create.CreateHabitFlow
import navigation.MainScreen
import screens.splash.SplashScreen
import themes.MainTheme

@Composable
fun App() {
    val repository = remember { Inject.instance<UserSettingsRepository>() }
    val settingsEventBus = remember { SettingsEventBus(repository) }
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

enum class AppScreens(val title: String) {
    Splash("splash"), Main("main"), Create("create"), Detail("detail")
}

@Composable
private fun JetHabitApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route ?: AppScreens.Splash.title

    CompositionLocalProvider(
        LocalNavHost provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = AppScreens.Splash.title
        ) {
            composable(route = AppScreens.Splash.title) {
                SplashScreen(navController)
            }

            composable(route = AppScreens.Main.title) {
                MainScreen()
            }

            composable(route = AppScreens.Create.title) {
                CreateHabitFlow()
            }
        }
    }
}