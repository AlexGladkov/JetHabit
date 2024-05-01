package screens.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import screens.daily.views.HabitCardItemModel
import screens.detail.models.DetailAction
import screens.detail.models.DetailEvent

@Composable
internal fun DetailScreen(
    cardModel: HabitCardItemModel,
    viewModel: DetailViewModel = viewModel { DetailViewModel(cardModel) }
) {

    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(null)

    DetailView(viewState, eventHandler = { viewModel.obtainEvent(it) })

    when (viewAction) {
        DetailAction.CloseScreen -> {} // rootController.popBackStack()
        DetailAction.DateError -> {
//            val configuration = AlertConfiguration(
//                cornerRadius = 4, maxHeight = 0.4f,
//                maxWidth = 0.7f, alpha = 0.5f
//            )
//            modalController.present(alertConfiguration = configuration) { key ->
//                Column(
//                    modifier = Modifier
//                        .clickable { modalController.popBackStack(key) }
//                        .fillMaxSize()
//                        .background(JetHabitTheme.colors.primaryBackground)
//                        .padding(16.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = AppRes.string.title_error,
//                        color = JetHabitTheme.colors.primaryText,
//                        style = JetHabitTheme.typography.heading
//                    )
//
//                    Text(
//                        modifier = Modifier.padding(top = 24.dp),
//                        text = AppRes.string.error_date,
//                        color = JetHabitTheme.colors.primaryText,
//                        style = JetHabitTheme.typography.body
//                    )
//                }
//            }

            viewModel.obtainEvent(DetailEvent.ActionInvoked)
        }

        null -> {}
    }
}