package screens.add_dates

import com.adeo.kviewmodel.BaseSharedViewModel
import com.soywiz.klock.DateTime
import data.features.medication.MedicationRepository
import di.Inject
import kotlinx.coroutines.launch
import screens.add_dates.models.MedicationAddDateCountType
import screens.add_dates.models.MedicationAddDatesAction
import screens.add_dates.models.MedicationAddDatesEvent
import screens.add_dates.models.MedicationAddDatesViewState
import tech.mobiledeveloper.shared.AppRes

class MedicationAddDatesViewModel(name: String) :
    BaseSharedViewModel<MedicationAddDatesViewState, MedicationAddDatesAction, MedicationAddDatesEvent>(
        initialState = MedicationAddDatesViewState(name = name)
    ) {

    private val medicationRepository: MedicationRepository = Inject.instance()

    override fun obtainEvent(viewEvent: MedicationAddDatesEvent) {
        when (viewEvent) {
            MedicationAddDatesEvent.ActionInvoked -> viewAction = null
            MedicationAddDatesEvent.AddStartDateClicked -> viewAction =
                MedicationAddDatesAction.PresentStartDate

            MedicationAddDatesEvent.FrequencyClicked -> performSetupFrequency()
            MedicationAddDatesEvent.PeriodicityClicked -> performSetupPeriodicity()
            MedicationAddDatesEvent.WeekCountClicked -> performSetupWeekCount()
            MedicationAddDatesEvent.AddNewMedicine -> addNewMedicine()
            is MedicationAddDatesEvent.PeriodicitySelected -> performSelectPeriodicity(viewEvent.value)
            is MedicationAddDatesEvent.FrequencySelected -> performSelectFrequency(viewEvent.value)
            is MedicationAddDatesEvent.StarDateSelected -> performSetupStartDate(viewEvent.value)
            is MedicationAddDatesEvent.CountSelected -> setupCountableValue(viewEvent.value)
        }
    }

    private fun performSetupFrequency() {
        viewAction = MedicationAddDatesAction.PresentFrequency
    }

    private fun performSetupPeriodicity() {
        viewAction = MedicationAddDatesAction.PresentPeriodicity
    }

    private fun performSetupWeekCount() {
        viewAction =
            MedicationAddDatesAction.PresentCountSelection(MedicationAddDateCountType.WeekCount)
    }

    private fun setupCountableValue(value: String) {
        viewState = viewState.copy(weekCount = value)
    }

    private fun performSetupStartDate(value: DateTime) {
        viewState = viewState.copy(startDate = value.format("dd MMM yyyy"), calendarDate = value)
    }

    private fun performSelectPeriodicity(values: List<Boolean>) {
        viewModelScope.launch {
            val containsZeroes = values.firstOrNull { !it } != null
            viewState = if (!containsZeroes) {
                viewState.copy(
                    periodicity = AppRes.string.medication_add_dates_every_day,
                    periodicityValues = values
                )
            } else {
                val builder = StringBuilder()
                values.forEachIndexed { index, i ->
                    if (i) {
                        when (index) {
                            0 -> builder.append(
                                AppRes.string.days_monday_short.lowercase().capitalize()
                            )

                            1 -> builder.append(
                                AppRes.string.days_tuesday_short.lowercase().capitalize()
                            )

                            2 -> builder.append(
                                AppRes.string.days_wednesday_short.lowercase().capitalize()
                            )

                            3 -> builder.append(
                                AppRes.string.days_thursday_short.lowercase().capitalize()
                            )

                            4 -> builder.append(
                                AppRes.string.days_friday_short.lowercase().capitalize()
                            )

                            5 -> builder.append(
                                AppRes.string.days_saturday_short.lowercase().capitalize()
                            )

                            6 -> builder.append(
                                AppRes.string.days_sunday_short.lowercase().capitalize()
                            )
                        }
                        builder.append(", ")
                    }
                }

                val result = builder.toString().dropLast(2)
                viewState.copy(periodicity = result, periodicityValues = values)
            }
        }
    }

    private fun performSelectFrequency(values: List<Boolean>) {
        viewModelScope.launch {
            val containsZeroes = values.firstOrNull { !it } != null
            viewState = if (!containsZeroes) {
                viewState.copy(
                    frequency = AppRes.string.medication_add_dates_all_day,
                    frequencyValues = values
                )
            } else {
                val builder = StringBuilder()
                values.forEachIndexed { index, i ->
                    if (i) {
                        when (index) {
                            0 -> builder.append(
                                AppRes.string.times_of_day_morning.lowercase().capitalize()
                            )

                            1 -> builder.append(
                                AppRes.string.times_of_day_afternoon.lowercase().capitalize()
                            )

                            2 -> builder.append(
                                AppRes.string.times_of_day_evening.lowercase().capitalize()
                            )
                        }
                        builder.append(", ")
                    }
                }

                val result = builder.toString().dropLast(2)
                viewState.copy(frequency = result, frequencyValues = values)
            }
        }
    }

    private fun addNewMedicine() {
        viewModelScope.launch {
            medicationRepository.createNewMedication(
                title = viewState.name,
                startDate = viewState.calendarDate,
                weekCount = viewState.weekCount.toInt(),
                frequency = viewState.frequencyValues,
                periodicity = viewState.periodicityValues
            )

            viewAction = MedicationAddDatesAction.CloseScreen
        }
    }
}