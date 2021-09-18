package ru.alexgladkov.jetpackcomposedemo.screens.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.alexgladkov.jetpackcomposedemo.screens.daily.DailyScreen
import ru.alexgladkov.jetpackcomposedemo.screens.daily.DailyViewModel
import ru.alexgladkov.jetpackcomposedemo.screens.main.MainBottomScreen

@ExperimentalFoundationApi
fun NavGraphBuilder.settingsFlow(
    navController: NavController,
    paddingValues: PaddingValues
) {
    navigation(startDestination = "settings", route = MainBottomScreen.Settings.route) {
        composable("settings") {
            val dailyViewModel = hiltViewModel<DailyViewModel>()
            DailyScreen(
                modifier = Modifier.padding(paddingValues),
                navController = navController, dailyViewModel = dailyViewModel
            )
        }
    }
}