package ru.alexgladkov.jetpackcomposedemo.screens.daily

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import ru.alexgladkov.jetpackcomposedemo.screens.daily.models.DailyEvent
import ru.alexgladkov.jetpackcomposedemo.screens.daily.models.DailyViewState
import ru.alexgladkov.jetpackcomposedemo.screens.daily.views.DailyViewDisplay
import ru.alexgladkov.jetpackcomposedemo.screens.daily.views.DailyViewError
import ru.alexgladkov.jetpackcomposedemo.screens.daily.views.DailyViewLoading
import ru.alexgladkov.jetpackcomposedemo.screens.daily.views.DailyViewNoItems

@ExperimentalFoundationApi
@Composable
fun DailyScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    dailyViewModel: DailyViewModel
) {
    val viewState = dailyViewModel.dailyViewState.observeAsState()

    when (val state = viewState.value) {
        DailyViewState.Loading -> DailyViewLoading()
        DailyViewState.NoItems -> DailyViewNoItems(
            onComposeClick = {
                navController.navigate(
                    route = "compose", navOptions = NavOptions.Builder()
                        .setEnterAnim(0)
                        .setExitAnim(0)
                        .setPopEnterAnim(0)
                        .setPopExitAnim(0)
                        .build()
                )
            }
        )
        DailyViewState.Error -> DailyViewError {
            dailyViewModel.obtainEvent(DailyEvent.ReloadScreen)
        }
        is DailyViewState.Display -> DailyViewDisplay(
            modifier = modifier,
            navController = navController,
            viewState = state,
            onPreviousDayClicked = { dailyViewModel.obtainEvent(DailyEvent.PreviousDayClicked) },
            onNextDayClicked = { dailyViewModel.obtainEvent(DailyEvent.NextDayClicked) },
            onCheckedChange = { itemId, isChecked ->
                dailyViewModel.obtainEvent(
                    DailyEvent.OnHabitClick(
                        itemId,
                        isChecked
                    )
                )
            }
        )
        else -> throw NotImplementedError("Unexpected daily state")
    }

    LaunchedEffect(key1 = viewState, block = {
        dailyViewModel.obtainEvent(event = DailyEvent.EnterScreen)
    })
}

