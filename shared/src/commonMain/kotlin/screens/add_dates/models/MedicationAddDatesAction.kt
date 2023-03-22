package screens.add_dates.models

enum class MedicationAddDateCountType {
    Frequency, WeekCount
}

sealed class MedicationAddDatesAction {
    object PresentStartDate : MedicationAddDatesAction()
    data class PresentCountSelection(val medicationAddDateCountType: MedicationAddDateCountType) :
        MedicationAddDatesAction()
}
