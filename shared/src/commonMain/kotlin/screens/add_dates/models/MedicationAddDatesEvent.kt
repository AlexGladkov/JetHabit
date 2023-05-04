package screens.add_dates.models

import com.soywiz.klock.DateTime


sealed class MedicationAddDatesEvent {
    object AddStartDateClicked : MedicationAddDatesEvent()
    object FrequencyClicked : MedicationAddDatesEvent()
    object PeriodicityClicked : MedicationAddDatesEvent()
    object WeekCountClicked : MedicationAddDatesEvent()
    object ActionInvoked : MedicationAddDatesEvent()
    object AddNewMedicine : MedicationAddDatesEvent()
    data class PeriodicitySelected(val value: List<Boolean>) : MedicationAddDatesEvent()
    data class FrequencySelected(val value: List<Boolean>) : MedicationAddDatesEvent()
    data class StarDateSelected(val value: DateTime) : MedicationAddDatesEvent()
    data class CountSelected(val type: MedicationAddDateCountType, val value: String) :
        MedicationAddDatesEvent()
}
