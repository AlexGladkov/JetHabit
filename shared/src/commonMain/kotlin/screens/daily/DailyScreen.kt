package screens.daily

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.adeo.kviewmodel.odyssey.StoredViewModel
import screens.daily.models.DailyEvent
import screens.daily.models.DailyViewState
import screens.daily.views.DailyViewDisplay
import screens.daily.views.DailyViewError
import screens.daily.views.DailyViewLoading
import screens.daily.views.DailyViewNoItems
import ru.alexgladkov.odyssey.compose.extensions.present
import ru.alexgladkov.odyssey.compose.extensions.push
import ru.alexgladkov.odyssey.compose.local.LocalRootController
import screens.daily.models.DailyAction

@ExperimentalFoundationApi
@Composable
internal fun DailyScreen() {
    val rootController = LocalRootController.current

    StoredViewModel(factory = { DailyViewModel() }) { viewModel ->
        val viewState = viewModel.viewStates().collectAsState()
        val viewAction by viewModel.viewActions().collectAsState(null)

        when (val state = viewState.value) {
            DailyViewState.Loading -> DailyViewLoading()
            DailyViewState.NoItems -> DailyViewNoItems(
                onComposeClick = {
                    rootController.findRootController().present("medication_add_flow")
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
                    rootController.push("detail", it)
                }
            )
        }

        when (viewAction) {
            is DailyAction.OpenDetail -> {
                rootController.push("detail")
                viewModel.obtainEvent(DailyEvent.CloseAction)
            }
            null -> {}
        }

        LaunchedEffect(key1 = viewState, block = {
            viewModel.obtainEvent(event = DailyEvent.EnterScreen)
        })
    }
}

