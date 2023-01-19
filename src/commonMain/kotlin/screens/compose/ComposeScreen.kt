package screens.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.adeo.kviewmodel.odyssey.StoredViewModel
import ru.alexgladkov.odyssey.compose.local.LocalRootController
import screens.compose.models.ComposeEvent
import screens.compose.models.ComposeViewState
import screens.compose.views.ComposeViewInitial
import screens.compose.views.ComposeViewSuccess

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun ComposeScreen() {
    val rootController = LocalRootController.current

    StoredViewModel(factory = { ComposeViewModel() }) { viewModel ->
        val viewState = viewModel.viewStates().collectAsState(initial = ComposeViewState.ViewStateInitial())
        val keyboardController = LocalSoftwareKeyboardController.current

        when (val state = viewState.value) {
            is ComposeViewState.ViewStateInitial -> ComposeViewInitial(
                state = state,
                onCheckedChange = { viewModel.obtainEvent(ComposeEvent.CheckboxClicked(it)) },
                onTitleChanged = { viewModel.obtainEvent(ComposeEvent.TitleChanged(it)) },
                onSaveClicked = {
                    keyboardController?.hide()
                    viewModel.obtainEvent(ComposeEvent.SaveClicked)
                }
            )

            is ComposeViewState.ViewStateSuccess -> ComposeViewSuccess(
                onCloseClick = { rootController.popBackStack() }
            )
        }
    }
}