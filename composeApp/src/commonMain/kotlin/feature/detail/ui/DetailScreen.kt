package feature.detail.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import feature.detail.presentation.DetailComponent
import feature.detail.presentation.models.DateSelectionState
import feature.detail.presentation.models.DetailEvent
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import themes.components.CCalendar
import ui.themes.JetHabitTheme
import kotlinx.datetime.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun DetailScreen(
    component: DetailComponent
) {
    val viewState by component.state.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )

    DetailView(viewState, eventHandler = { component.onEvent(it) })

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
                    component.onEvent(DetailEvent.DateSelected(it))
                }
            )
        },
        scrimColor = Color.Black.copy(alpha = 0.2f),
        content = {

        }
    )
}