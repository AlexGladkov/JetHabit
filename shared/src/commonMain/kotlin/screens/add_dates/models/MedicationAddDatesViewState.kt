package screens.add_dates.models

import com.soywiz.klock.DateTime
import tech.mobiledeveloper.shared.AppRes

data class MedicationAddDatesViewState(
    val name: String = "",
    val frequency: String = AppRes.string.medication_add_dates_all_day,
    val frequencyValues: List<Boolean> = listOf(true, true, true),
    val weekCount: String = "1",
    val periodicity: String = AppRes.string.medication_add_dates_every_day,
    val periodicityValues: List<Boolean> = listOf(true, true, true, true, true, true, true),
    val startDate: String? = null,
    val calendarDate: DateTime = DateTime.now()
)

