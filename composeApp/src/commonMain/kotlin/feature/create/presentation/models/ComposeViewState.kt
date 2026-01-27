package feature.create.presentation.models

import feature.habits.data.HabitType
import feature.habits.data.Measurement
import feature.projects.data.ProjectEntity
import kotlinx.datetime.LocalDate

data class ComposeViewState(
    val habitTitle: String = "",
    val isGoodHabit: Boolean = true,
    val isSending: Boolean = false,
    val habitType: HabitType = HabitType.REGULAR,
    val measurement: Measurement = Measurement.KILOGRAMS,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val showStartDatePicker: Boolean = false,
    val showEndDatePicker: Boolean = false,
    val selectedProjectId: String? = null,
    val projects: List<ProjectEntity> = emptyList()
) 