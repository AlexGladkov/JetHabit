package screens.daily

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.adeo.kviewmodel.odyssey.StoredViewModel
import ru.alexgladkov.jetpackcomposedemo.screens.daily.models.DailyEvent
import screens.daily.models.DailyViewState
import screens.daily.views.DailyViewDisplay
import ru.alexgladkov.jetpackcomposedemo.screens.daily.views.DailyViewError
import screens.daily.views.DailyViewLoading
import screens.daily.views.DailyViewNoItems
import ru.alexgladkov.odyssey.compose.extensions.present
import ru.alexgladkov.odyssey.compose.local.LocalRootController

@ExperimentalFoundationApi
@Composable
fun DailyScreen() {
    val rootController = LocalRootController.current

    StoredViewModel(factory = { DailyViewModel() }) { viewModel ->
        val viewState = viewModel.viewStates().collectAsState()

        when (val state = viewState.value) {
            DailyViewState.Loading -> DailyViewLoading()
            DailyViewState.NoItems -> DailyViewNoItems(
                onComposeClick = {
                    rootController.findRootController().present("compose")
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
                }
            )
        }

        LaunchedEffect(key1 = viewState, block = {
            viewModel.obtainEvent(event = DailyEvent.EnterScreen)
        })
    }
}

