package screens.create

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import screens.add_dates.MedicationAddDates
import screens.add_name.MedicationAddName
import screens.compose.ComposeScreen

enum class CreateFlowScreens(val title: String) {
    Start("start"), AddName("add_name"), AddDate("add_date")
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun CreateHabitFlow() {
    val navigationController = rememberNavController()
    val backStackEntry by navigationController.currentBackStackEntryAsState()

    NavHost(
        navController = navigationController,
        startDestination = CreateFlowScreens.Start.title
    ) {
        createHabitFlow()
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
fun NavGraphBuilder.createHabitFlow() {
    composable(route = CreateFlowScreens.Start.title) {
        ComposeScreen()
    }

    composable(route = CreateFlowScreens.AddName.title) {
        MedicationAddName()
    }

    composable(route = CreateFlowScreens.AddDate.title) {
        MedicationAddDates("Test")
    }
}