package feature.daily.ui

import AppScreens
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import navigation.LocalNavHost
import feature.daily.ui.models.DailyAction
import feature.daily.presentation.DailyViewModel
import feature.daily.ui.models.DailyEvent
import navigation.DailyScreens

@ExperimentalFoundationApi
@Composable
internal fun DailyScreen(
    navController: NavController,
    viewModel: DailyViewModel = viewModel { DailyViewModel() }
) {
    val outerNavController = LocalNavHost.current
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(null)

    DailyView(viewState = viewState) {
        viewModel.obtainEvent(it)
    }

    when (viewAction) {
        is DailyAction.OpenDetail -> {
            navController.navigate("${DailyScreens.Detail.name}/${(viewAction as DailyAction.OpenDetail).itemId}")
            viewModel.clearAction()
        }

        DailyAction.OpenCompose -> {
            outerNavController.navigate(AppScreens.Create.title)
            viewModel.clearAction()
        }

        null -> {}
    }

    LaunchedEffect(Unit) {
        viewModel.obtainEvent(DailyEvent.ReloadScreen)
    }
}

