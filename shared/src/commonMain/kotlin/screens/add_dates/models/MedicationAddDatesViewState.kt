package screens.add_dates.models

data class MedicationAddDatesViewState(
    val name: String = "",
    val frequency: String = "1",
    val weekCount: String = "1",
    val startDate: String? = null
)
