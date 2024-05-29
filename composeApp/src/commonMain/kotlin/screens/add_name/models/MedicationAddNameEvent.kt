package screens.add_name.models

sealed class MedicationAddNameEvent {
    data class ChangeName(val value: String) : MedicationAddNameEvent()
    data object NextClicked : MedicationAddNameEvent()
    data object ActionInvoked : MedicationAddNameEvent()
}