package feature.create.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.viewmodel.compose.viewModel
import feature.create.presentation.ComposeViewModel
import feature.create.presentation.models.ComposeEvent
import feature.habits.data.HabitType
import navigation.LocalNavHost
import screens.compose.models.ComposeAction

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
internal fun ComposeScreen(
    type: String? = null,
    viewModel: ComposeViewModel = viewModel { ComposeViewModel() }
) {
    val outerNavigation = LocalNavHost.current
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(null)
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(type) {
        if (type == "tracker") {
            viewModel.obtainEvent(ComposeEvent.TypeSelected(HabitType.TRACKER))
        }
    }

    ComposeView(viewState = viewState) {
        viewModel.obtainEvent(it)
    }

    when (viewAction) {
        ComposeAction.Success -> {
            keyboardController?.hide()
            outerNavigation.popBackStack()
            viewModel.clearAction()
        }
        ComposeAction.Error -> {
            viewModel.clearAction()
        }
        ComposeAction.CloseScreen -> {
            keyboardController?.hide()
            outerNavigation.popBackStack()
            viewModel.clearAction()
        }
        null -> {}
    }
}