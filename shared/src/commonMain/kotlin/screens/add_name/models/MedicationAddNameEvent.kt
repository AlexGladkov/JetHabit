package screens.add_name.models

sealed class MedicationAddNameEvent {
    data class ChangeName(val value: String) : MedicationAddNameEvent()
    object NextClicked : MedicationAddNameEvent()
    object ActionInvoked : MedicationAddNameEvent()
}