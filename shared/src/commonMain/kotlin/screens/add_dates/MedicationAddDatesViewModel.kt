package screens.add_dates

import com.adeo.kviewmodel.BaseSharedViewModel
import screens.add_dates.models.MedicationAddDatesAction
import screens.add_dates.models.MedicationAddDatesEvent
import screens.add_dates.models.MedicationAddDatesViewState

class MedicationAddDatesViewModel: BaseSharedViewModel<MedicationAddDatesViewState, MedicationAddDatesAction, MedicationAddDatesEvent>(
    initialState = MedicationAddDatesViewState()
) {

    override fun obtainEvent(viewEvent: MedicationAddDatesEvent) {
        when (viewEvent) {
            MedicationAddDatesEvent.ActionInvoked -> viewAction = null
            MedicationAddDatesEvent.AddStartDateClicked -> viewAction = MedicationAddDatesAction.PresentStartDate
            MedicationAddDatesEvent.FrequencyClicked -> TODO()
            MedicationAddDatesEvent.PeriodicityClicked -> TODO()
            MedicationAddDatesEvent.WeekCountClicked -> TODO()
            is MedicationAddDatesEvent.StarDateSelected -> TODO()
        }
    }
}