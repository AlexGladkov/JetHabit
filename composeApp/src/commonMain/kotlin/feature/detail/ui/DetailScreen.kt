package screens.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import screens.daily.views.HabitCardItemModel
import feature.detail.presentation.models.DetailAction
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import themes.components.CCalendar
import ui.themes.JetHabitShape
import ui.themes.JetHabitTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun DetailScreen(
    cardModel: HabitCardItemModel,
    viewModel: DetailViewModel = viewModel { DetailViewModel(cardModel) }
) {
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(null)
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )

    DetailView(viewState, eventHandler = { viewModel.obtainEvent(it) })

    when (viewAction) {
        DetailAction.CloseScreen -> {} // rootController.popBackStack()
        DetailAction.DateError -> {
            viewModel.clearAction()
        }

        is DetailAction.ShowCalendar -> {
            println("Show calendar")
            coroutineScope.launch {
                bottomSheetState.show()
            }
        }

        null -> {}
    }

    ModalBottomSheetLayout(
        modifier = Modifier.fillMaxSize(),
        sheetState = bottomSheetState,
        sheetContent = {
            CCalendar(
                selectedDate = LocalDate.parse("2024-05-24"),
                selectedColor = JetHabitTheme.colors.tintColor,
                dayOfWeekColor = JetHabitTheme.colors.errorColor,
                textColor = JetHabitTheme.colors.primaryText,
                onDateSelected = {
                    
                }
            )
        },
        scrimColor = Color.Black.copy(alpha = 0.2f),
        content = {
            Text("Hello, bottom sheet")
        }
    )
}