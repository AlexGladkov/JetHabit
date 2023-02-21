package screens.detail

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.adeo.kviewmodel.odyssey.StoredViewModel
import ru.alexgladkov.odyssey.compose.local.LocalRootController
import screens.daily.views.HabitCardItemModel
import screens.detail.models.DetailAction
import screens.detail.models.DetailEvent

@Composable
internal fun DetailScreen(cardModel: HabitCardItemModel) {
    val rootController = LocalRootController.current

    StoredViewModel(factory = { DetailViewModel(cardModel) }) { viewModel ->
        val viewState by viewModel.viewStates().collectAsState()
        val viewAction by viewModel.viewActions().collectAsState(null)

        DetailView(viewState, onCloseClicked = {
            viewModel.obtainEvent(DetailEvent.CloseScreen)
        }, onDeleteItemClicked = {
            viewModel.obtainEvent(DetailEvent.DeleteItem)
        })

        when (viewAction) {
            DetailAction.CloseScreen -> rootController.popBackStack()
            null -> {}
        }
    }
}