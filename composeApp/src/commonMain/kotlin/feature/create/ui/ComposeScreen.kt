package feature.create.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import feature.create.presentation.CreateHabitComponent

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
internal fun ComposeScreen(
    component: CreateHabitComponent
) {
    val viewState by component.state.subscribeAsState()

    ComposeView(viewState = viewState) {
        component.onEvent(it)
    }
}