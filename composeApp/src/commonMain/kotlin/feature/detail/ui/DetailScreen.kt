package feature.detail.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import feature.detail.presentation.DetailViewModel
import feature.detail.presentation.models.DateSelectionState
import feature.detail.presentation.models.DetailAction
import feature.detail.presentation.models.DetailEvent
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import themes.components.CCalendar
import ui.themes.JetHabitTheme
import kotlinx.datetime.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun DetailScreen(
    habitId: String,
    navController: NavController,
    viewModel: DetailViewModel = viewModel { DetailViewModel(habitId) }
) {
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(null)
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )

    DetailView(viewState, eventHandler = { viewModel.obtainEvent(it) })

    when (viewState.dateSelectionState) {
        DateSelectionState.None ->
            coroutineScope.launch {
                bottomSheetState.hide()
            }

        DateSelectionState.Start ->
            coroutineScope.launch {
                bottomSheetState.show()
            }

        DateSelectionState.End ->
            coroutineScope.launch {
                bottomSheetState.show()
            }
    }

    when (viewAction) {
        DetailAction.CloseScreen -> navController.popBackStack()
        DetailAction.DateError -> {
            viewModel.clearAction()
        }

        null -> {}
    }

    ModalBottomSheetLayout(
        modifier = Modifier.fillMaxSize(),
        sheetState = bottomSheetState,
        sheetContent = {
            val currentTimeZone = TimeZone.currentSystemDefault()
            val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val selectedDate = when (viewState.dateSelectionState) {
                DateSelectionState.None -> currentDate
                DateSelectionState.Start -> viewState.start ?: currentDate
                DateSelectionState.End -> viewState.end ?: currentDate
            }

            CCalendar(
                selectedDate = selectedDate,
                selectedColor = JetHabitTheme.colors.tintColor,
                dayOfWeekColor = JetHabitTheme.colors.errorColor,
                textColor = JetHabitTheme.colors.primaryText,
                onDateSelected = {
                    viewModel.obtainEvent(DetailEvent.DateSelected(it))
                }
            )
        },
        scrimColor = Color.Black.copy(alpha = 0.2f),
        content = {

        }
    )
}