package screens.add_dates.models

import com.soywiz.klock.DateTime
import tech.mobiledeveloper.shared.AppRes

data class MedicationAddDatesViewState(
    val name: String = "",
    val frequency: String = "1",
    val weekCount: String = "1",
    val periodicity: String = AppRes.string.medication_add_dates_every_day,
    val periodicityValues: List<Int> = listOf(1, 1, 1, 1, 1, 1, 1),
    val startDate: String? = null,
    val calendarDate: DateTime = DateTime.now()
)
