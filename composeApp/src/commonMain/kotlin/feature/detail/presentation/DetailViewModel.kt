package screens.detail

import base.BaseViewModel
import com.soywiz.klock.DateTime
import data.features.medication.MedicationRepository
import di.Inject
import feature.detail.presentation.models.DateSelectionState
import screens.daily.views.HabitCardItemModel
import feature.detail.presentation.models.DetailAction
import feature.detail.presentation.models.DetailEvent
import feature.detail.presentation.models.DetailViewState

class DetailViewModel(private val habitId: Long): BaseViewModel<DetailViewState, DetailAction, DetailEvent>(
    initialState = DetailViewState(habitId = habitId)
) {

    private val medicationRepository = Inject.instance<MedicationRepository>()

    init {
        fetchDetailedInformation()
    }

    override fun obtainEvent(viewEvent: DetailEvent) {
        when (viewEvent) {
            DetailEvent.CloseScreen -> viewAction = DetailAction.CloseScreen
            DetailEvent.DeleteItem -> deleteItem()
            DetailEvent.SaveChanges -> applyChanges()
            is DetailEvent.EndDateSelected -> setEndDate(viewEvent.value)
            is DetailEvent.StartDateSelected -> setStartDate(viewEvent.value)
            DetailEvent.StartDateClicked -> viewState = viewState.copy(dateSelectionState = DateSelectionState.Start)
            DetailEvent.EndDateClicked -> viewState = viewState.copy(dateSelectionState = DateSelectionState.End)
        }
    }

    private fun fetchDetailedInformation() {
//        viewModelScope.launch(Dispatchers.Default) {
//            val medication = medicationRepository.fetchCurrentMedications().first { it.itemId == cardModel.habitId }
//            val startDate = getValueOrNull(medication.startDate)
//            val endDate = getValueOrNull(medication.endDate)
//
//            withContext(Dispatchers.Main) {
//
//                viewState = viewState.copy(
//                    itemTitle = medication.title,
//                    startDate = startDate?.toString("dd MMM yyyy") ?: AppRes.string.not_selected,
//                    endDate = endDate?.toString("dd MMM yyyy") ?: AppRes.string.not_selected,
//                    start = startDate?.local,
//                    end = endDate?.local
//                )
//            }
//        }
    }

    private fun deleteItem() {
//        viewModelScope.launch {
//            viewState = viewState.copy(isDeleting = true)
//            medicationRepository.deleteItem(cardModel.habitId)
//            viewAction = DetailAction.CloseScreen
//        }
    }

    private fun setStartDate(value: DateTime) {
//        viewState = viewState.copy(
//            startDate = value.format("dd MMM yyyy"),
//            start = value
//        )
    }

    private fun setEndDate(value: DateTime) {
//        viewState = viewState.copy(
//            endDate = value.toString("dd MMM yyyy"),
//            end = value
//        )
    }

    private fun applyChanges() {
//        val startDate = viewState.start
//        val endDate = viewState.end
//
//        if (startDate == null || endDate == null) {
//            viewAction = DetailAction.CloseScreen
//        } else {
//            if (startDate.compareTo(endDate) == -1) {
//                updateData(startDate, endDate)
//            } else {
//                viewAction = DetailAction.DateError
//            }
//        }
    }

    private fun updateData(startDate: DateTime, endDate: DateTime) {
//        viewModelScope.launch(Dispatchers.Default) {
//            medicationRepository.updateMedication(cardModel.habitId, startDate, endDate)
//            viewAction = DetailAction.CloseScreen
//        }
    }
}