import androidx.compose.runtime.Composable

@Composable
fun JetHabitApp() {
//    val isDarkModeValue = true // isSystemInDarkTheme()
//
//    val currentStyle = remember { mutableStateOf(JetHabitStyle.Purple) }
//    val currentFontSize = remember { mutableStateOf(JetHabitSize.Medium) }
//    val currentPaddingSize = remember { mutableStateOf(JetHabitSize.Medium) }
//    val currentCornersStyle = remember { mutableStateOf(JetHabitCorners.Rounded) }
//    val isDarkMode = remember { mutableStateOf(isDarkModeValue) }
//
//    MainTheme(
//        style = currentStyle.value,
//        darkTheme = isDarkMode.value,
//        textSize = currentFontSize.value,
//        corners = currentCornersStyle.value,
//        paddingSize = currentPaddingSize.value
//    ) {
//        val navController = rememberNavController()
//        val systemUiController = rememberSystemUiController()
//
//        // Set status bar color
//        SideEffect {
//            systemUiController.setSystemBarsColor(
//                color = if (isDarkMode.value) baseDarkPalette.primaryBackground else baseLightPalette.primaryBackground,
//                darkIcons = !isDarkMode.value
//            )
//        }
//
//        Surface {
//            NavHost(navController = navController, startDestination = "splash") {
//                composable("splash") {
//                    SplashScreen(navController = navController)
//                }
//
//                composable("main") {
//                    val settings = SettingsBundle(
//                        isDarkMode = isDarkMode.value,
//                        style = currentStyle.value,
//                        textSize = currentFontSize.value,
//                        cornerStyle = currentCornersStyle.value,
//                        paddingSize = currentPaddingSize.value
//                    )
//
//                    MainScreen(navController = navController,
//                        settings = settings, onSettingsChanged = {
//                            isDarkMode.value = it.isDarkMode
//                            currentStyle.value = it.style
//                            currentFontSize.value = it.textSize
//                            currentCornersStyle.value = it.cornerStyle
//                            currentPaddingSize.value = it.paddingSize
//                        }
//                    )
//                }
//
//                composable("compose") {
//                    val composeViewModel = hiltViewModel<ComposeViewModel>()
//                    ComposeScreen(navController = navController, composeViewModel = composeViewModel)
//                }
//            }
//        }
//    }
}