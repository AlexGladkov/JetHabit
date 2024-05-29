package screens.add_dates.models

enum class MedicationAddDateCountType {
    Frequency, WeekCount
}

sealed class MedicationAddDatesAction {
    object PresentStartDate : MedicationAddDatesAction()
    object PresentPeriodicity : MedicationAddDatesAction()
    object PresentFrequency : MedicationAddDatesAction()
    object CloseScreen : MedicationAddDatesAction()
    data class PresentCountSelection(val medicationAddDateCountType: MedicationAddDateCountType) :
        MedicationAddDatesAction()
}
