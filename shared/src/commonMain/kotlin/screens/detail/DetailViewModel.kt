package screens.detail

import com.adeo.kviewmodel.BaseSharedViewModel
import data.features.habit.HabitRepository
import di.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import screens.daily.views.HabitCardItemModel
import screens.detail.models.DetailAction
import screens.detail.models.DetailEvent
import screens.detail.models.DetailViewState
import tech.mobiledeveloper.shared.AppRes
import utils.printDate
import utils.shrinkMonthName

class DetailViewModel(private val cardModel: HabitCardItemModel): BaseSharedViewModel<DetailViewState, DetailAction, DetailEvent>(
    initialState = DetailViewState(itemTitle = cardModel.title)
) {

    private val habitRepository: HabitRepository = Inject.instance()

    init {
        fetchDetailedInformation()
    }

    override fun obtainEvent(viewEvent: DetailEvent) {
        when (viewEvent) {
            DetailEvent.CloseScreen -> viewAction = DetailAction.CloseScreen
            DetailEvent.DeleteItem -> deleteItem()
            DetailEvent.SaveChanges -> applyChanges()
            DetailEvent.ActionInvoked -> viewAction = null
            is DetailEvent.EndDateSelected -> setEndDate(viewEvent.value)
            is DetailEvent.StartDateSelected -> setStartDate(viewEvent.value)
        }
    }

    private fun fetchDetailedInformation() {
        viewModelScope.launch(Dispatchers.Default) {
            val habit = habitRepository.fetchHabitsList().first { it.itemId == cardModel.habitId }
            val localStartDate = habit.startDate?.toLocalDateTime(TimeZone.currentSystemDefault())
            val localEndDate = habit.endDate?.toLocalDateTime(TimeZone.currentSystemDefault())
            withContext(Dispatchers.Main) {
                viewState = viewState.copy(
                    itemTitle = habit.title,
                    startDate = localStartDate?.printDate() ?: AppRes.string.not_selected,
                    endDate = localEndDate?.printDate() ?: AppRes.string.not_selected,
                    start = localStartDate,
                    end = localEndDate
                )
            }
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
            startDate = "${localDateTime.shrinkMonthName()} ${localDateTime.dayOfMonth}, ${localDateTime.year}",
            start = localDateTime
        )
    }

    private fun setEndDate(value: Instant) {
        val localDateTime = value.toLocalDateTime(TimeZone.currentSystemDefault())
        viewState = viewState.copy(
            endDate = "${localDateTime.shrinkMonthName()} ${localDateTime.dayOfMonth}, ${localDateTime.year}",
            end = localDateTime
        )
    }

    private fun applyChanges() {
        val startDate = viewState.start
        val endDate = viewState.end

        if (startDate == null || endDate == null) {
            viewAction = DetailAction.CloseScreen
        } else {
            if (startDate.year > endDate.year) {
                viewAction = DetailAction.DateError
            } else if (startDate.monthNumber > endDate.monthNumber) {
                viewAction = DetailAction.DateError
            } else if (startDate.dayOfMonth > endDate.dayOfMonth) {
                viewAction = DetailAction.DateError
            } else {
                updateData(startDate.toInstant(TimeZone.currentSystemDefault()), endDate.toInstant(TimeZone.currentSystemDefault()))
            }
        }
    }

    private fun updateData(startDate: Instant, endDate: Instant) {
        viewModelScope.launch(Dispatchers.Default) {
            habitRepository.updateDates(startDate = startDate, endDate = endDate, id = cardModel.habitId)
            viewAction = DetailAction.CloseScreen
        }
    }
}