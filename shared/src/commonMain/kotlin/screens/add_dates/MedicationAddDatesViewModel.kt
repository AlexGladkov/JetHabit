package screens.add_dates

import com.adeo.kviewmodel.BaseSharedViewModel
import com.soywiz.klock.DateTime
import kotlinx.datetime.Instant
import screens.add_dates.models.MedicationAddDateCountType
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
            MedicationAddDatesEvent.FrequencyClicked -> performSetupFrequency()
            MedicationAddDatesEvent.PeriodicityClicked -> performSetupPeriodicity()
            MedicationAddDatesEvent.WeekCountClicked -> performSetupWeekCount()
            is MedicationAddDatesEvent.StarDateSelected -> performSetupStartDate(viewEvent.value)
            is MedicationAddDatesEvent.CountSelected -> setupCountableValue(viewEvent.type, viewEvent.value)
        }
    }

    private fun performSetupFrequency() {
        viewAction = MedicationAddDatesAction.PresentCountSelection(MedicationAddDateCountType.Frequency)
    }

    private fun performSetupPeriodicity() {

    }

    private fun performSetupWeekCount() {
        viewAction = MedicationAddDatesAction.PresentCountSelection(MedicationAddDateCountType.WeekCount)
    }

    private fun setupCountableValue(type: MedicationAddDateCountType, value: String) {
        viewState = when (type) {
            MedicationAddDateCountType.Frequency -> viewState.copy(frequency = value)
            MedicationAddDateCountType.WeekCount -> viewState.copy(weekCount = value)
        }
    }

    private fun performSetupStartDate(value: Instant) {
        viewState = viewState.copy(startDate = value.toString())
    }
}