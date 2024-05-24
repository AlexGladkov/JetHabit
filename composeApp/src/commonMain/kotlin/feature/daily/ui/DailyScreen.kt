package feature.daily.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import navigation.LocalNavHost
import feature.daily.ui.models.DailyEvent
import screens.daily.models.DailyAction
import feature.daily.presentation.DailyViewModel

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

        null -> {}
    }
}

