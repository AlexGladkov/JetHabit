package screens.add_dates.models

import kotlinx.datetime.Instant

sealed class MedicationAddDatesEvent {
    object AddStartDateClicked : MedicationAddDatesEvent()
    object FrequencyClicked : MedicationAddDatesEvent()
    object PeriodicityClicked : MedicationAddDatesEvent()
    object WeekCountClicked : MedicationAddDatesEvent()
    object ActionInvoked : MedicationAddDatesEvent()
    object AddNewMedicine : MedicationAddDatesEvent()
    data class PeriodicitySelected(val value: List<Int>) : MedicationAddDatesEvent()
    data class StarDateSelected(val value: Instant) : MedicationAddDatesEvent()
    data class CountSelected(val type: MedicationAddDateCountType, val value: String) :
        MedicationAddDatesEvent()
}
