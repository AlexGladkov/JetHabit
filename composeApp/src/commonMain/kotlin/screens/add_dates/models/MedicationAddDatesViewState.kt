package screens.add_dates.models

import com.soywiz.klock.DateTime
import org.jetbrains.compose.resources.StringResource
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.medication_add_dates_all_day
import tech.mobiledeveloper.jethabit.resources.medication_add_dates_every_day

data class MedicationAddDatesViewState(
    val name: String = "",
    val frequency: StringResource = Res.string.medication_add_dates_all_day,
    val frequencyValues: List<Boolean> = listOf(true, true, true),
    val weekCount: String = "1",
    val periodicity: StringResource = Res.string.medication_add_dates_every_day,
    val periodicityValues: List<Boolean> = listOf(true, true, true, true, true, true, true),
    val startDate: String? = null,
    val calendarDate: DateTime = DateTime.now()
)

