import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import navigation.LocalNavHost
import navigation.NavigationScreens
import screens.create.CreateHabitFlow
import screens.main.MainScreen
import screens.splash.SplashScreen
import ui.themes.MainTheme

@Composable
fun App() {
    MainTheme {
        JetHabitApp()
    }
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