package screens.detail

import com.adeo.kviewmodel.BaseSharedViewModel
import data.features.habit.HabitRepository
import di.Inject
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import screens.daily.views.HabitCardItemModel
import screens.detail.models.DetailAction
import screens.detail.models.DetailEvent
import screens.detail.models.DetailViewState
import utils.shrinkMonthName

class DetailViewModel(private val cardModel: HabitCardItemModel): BaseSharedViewModel<DetailViewState, DetailAction, DetailEvent>(
    initialState = DetailViewState(itemTitle = cardModel.title)
) {

    private val habitRepository: HabitRepository = Inject.instance()

    override fun obtainEvent(viewEvent: DetailEvent) {
        when (viewEvent) {
            DetailEvent.CloseScreen -> viewAction = DetailAction.CloseScreen
            DetailEvent.DeleteItem -> deleteItem()
            is DetailEvent.EndDateSelected -> setEndDate(viewEvent.value)
            is DetailEvent.StartDateSelected -> setStartDate(viewEvent.value)
        }
    }

    private fun deleteItem() {
        viewModelScope.launch {
            viewState = viewState.copy(isDeleting = true)
            habitRepository.deleteItem(cardModel.habitId)
            viewAction = DetailAction.CloseScreen
        }
    }

    private fun setStartDate(value: Instant) {
        val localDateTime = value.toLocalDateTime(TimeZone.currentSystemDefault())
        viewState = viewState.copy(
            startDate = "${localDateTime.shrinkMonthName()} ${localDateTime.dayOfMonth}, ${localDateTime.year}"
        )
    }

    private fun setEndDate(value: Instant) {
        val localDateTime = value.toLocalDateTime(TimeZone.currentSystemDefault())
        viewState = viewState.copy(
            endDate = "${localDateTime.shrinkMonthName()} ${localDateTime.dayOfMonth}, ${localDateTime.year}"
        )
    }
}