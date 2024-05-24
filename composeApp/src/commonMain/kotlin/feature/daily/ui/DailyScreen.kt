package feature.daily.ui

import NavigationScreens
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import navigation.LocalNavHost
import feature.daily.ui.models.DailyAction
import feature.daily.presentation.DailyViewModel
import feature.daily.ui.models.DailyEvent

@ExperimentalFoundationApi
@Composable
internal fun DailyScreen(
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
            viewModel.clearAction()
        }

        DailyAction.OpenCompose -> {
            outerNavController.navigate(NavigationScreens.Create.title)
            viewModel.clearAction()
        }

        null -> {}
    }

    LaunchedEffect(Unit) {
        viewModel.obtainEvent(DailyEvent.ReloadScreen)
    }
}

