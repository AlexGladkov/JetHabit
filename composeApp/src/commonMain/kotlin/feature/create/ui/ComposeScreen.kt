package screens.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.viewmodel.compose.viewModel
import feature.create.presentation.ComposeViewModel
import feature.create.ui.ComposeView
import navigation.LocalNavHost
import screens.compose.models.ComposeAction
import screens.compose.models.ComposeEvent
import screens.compose.models.ComposeViewState
import screens.compose.views.ComposeViewSuccess

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
internal fun ComposeScreen(
    viewModel: ComposeViewModel = viewModel { ComposeViewModel() }
) {
    val outerNavigation = LocalNavHost.current
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(null)
    val keyboardController = LocalSoftwareKeyboardController.current

    ComposeView(viewState = viewState) {
        viewModel.obtainEvent(it)
    }

    when (viewAction) {
        ComposeAction.CloseScreen -> {
            viewModel.clearAction()
            outerNavigation.popBackStack()
        }
        ComposeAction.ShowSuccess -> {
            viewModel.clearAction()
            outerNavigation.popBackStack()
        }
        null -> {}
    }
}