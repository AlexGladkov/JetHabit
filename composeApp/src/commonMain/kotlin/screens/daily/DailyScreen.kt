package screens.daily

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import navigation.LocalNavHost
import screens.daily.models.DailyEvent
import screens.daily.models.DailyViewState
import screens.daily.views.DailyViewDisplay
import screens.daily.views.DailyViewError
import screens.daily.views.DailyViewLoading
import screens.daily.views.DailyViewNoItems
import screens.daily.models.DailyAction
import screens.detail.DailyViewModel

@ExperimentalFoundationApi
@Composable
internal fun DailyScreen(
    viewModel: DailyViewModel = viewModel { DailyViewModel() }
) {
    val outerNavController = LocalNavHost.current
    val viewState = viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(null)

    when (val state = viewState.value) {
        DailyViewState.Loading -> DailyViewLoading()
        DailyViewState.NoItems -> DailyViewNoItems(
            onComposeClick = {
                outerNavController.navigate(NavigationScreens.Create.title)
            }
        )

        DailyViewState.Error -> DailyViewError {
            viewModel.obtainEvent(DailyEvent.ReloadScreen)
        }

        is DailyViewState.Display -> DailyViewDisplay(
            viewState = state,
            onPreviousDayClicked = { viewModel.obtainEvent(DailyEvent.PreviousDayClicked) },
            onNextDayClicked = { viewModel.obtainEvent(DailyEvent.NextDayClicked) },
            onCheckedChange = { itemId, isChecked ->
                viewModel.obtainEvent(
                    DailyEvent.OnHabitClick(
                        itemId,
                        isChecked
                    )
                )
            },
            onItemClicked = {

            }
        )
    }

    when (viewAction) {
        is DailyAction.OpenDetail -> {
            viewModel.obtainEvent(DailyEvent.CloseAction)
        }

        null -> {}
    }

    LaunchedEffect(key1 = viewState, block = {
        viewModel.obtainEvent(event = DailyEvent.EnterScreen)
    })
}

