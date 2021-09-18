package ru.alexgladkov.jetpackcomposedemo.screens.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import ru.alexgladkov.jetpackcomposedemo.screens.compose.ComposeScreen
import ru.alexgladkov.jetpackcomposedemo.screens.compose.ComposeViewModel
import ru.alexgladkov.jetpackcomposedemo.screens.daily.DailyScreen
import ru.alexgladkov.jetpackcomposedemo.screens.daily.DailyViewModel
import ru.alexgladkov.jetpackcomposedemo.screens.main.MainBottomScreen

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
fun NavGraphBuilder.dailyFlow(
    navController: NavController,
) {
    navigation(startDestination = "daily", route = MainBottomScreen.Daily.route) {
        composable("daily") {
            val dailyViewModel = hiltViewModel<DailyViewModel>()
            DailyScreen(
                navController = navController, dailyViewModel = dailyViewModel
            )
        }

        composable("compose") {
            val composeViewModel = hiltViewModel<ComposeViewModel>()
            ComposeScreen(
                navController = navController,
                composeViewModel = composeViewModel
            )
        }
    }
}