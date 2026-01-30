package feature.daily.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import feature.daily.presentation.DailyListComponent
import feature.daily.ui.models.DailyEvent

@ExperimentalFoundationApi
@Composable
internal fun DailyScreen(
    component: DailyListComponent
) {
    val viewState by component.state.subscribeAsState()

    DailyView(viewState = viewState) {
        component.onEvent(it)
    }

    LaunchedEffect(Unit) {
        component.onEvent(DailyEvent.ReloadScreen)
    }
}

