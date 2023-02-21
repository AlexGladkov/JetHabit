package screens.detail

import com.adeo.kviewmodel.BaseSharedViewModel
import data.features.habit.HabitRepository
import di.Inject
import kotlinx.coroutines.launch
import screens.daily.views.HabitCardItemModel
import screens.detail.models.DetailAction
import screens.detail.models.DetailEvent
import screens.detail.models.DetailViewState

class DetailViewModel(private val cardModel: HabitCardItemModel): BaseSharedViewModel<DetailViewState, DetailAction, DetailEvent>(
    initialState = DetailViewState(itemTitle = cardModel.title)
) {

    private val habitRepository: HabitRepository = Inject.instance()

    override fun obtainEvent(viewEvent: DetailEvent) {
        when (viewEvent) {
            DetailEvent.CloseScreen -> viewAction = DetailAction.CloseScreen
            DetailEvent.DeleteItem -> deleteItem()
        }
    }

    private fun deleteItem() {
        viewModelScope.launch {
            viewState = viewState.copy(isDeleting = true)
            habitRepository.deleteItem(cardModel.habitId)
            viewAction = DetailAction.CloseScreen
        }
    }
}