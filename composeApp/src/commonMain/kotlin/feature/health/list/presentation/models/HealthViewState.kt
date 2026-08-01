package feature.health.list.presentation.models

import feature.projects.data.ProjectEntity

data class HealthViewState(
    val habits: List<TrackerHabitItem> = emptyList(),
    val projects: List<ProjectEntity> = emptyList(),
    val selectedProjectId: String? = null,
    val isUncategorizedSelected: Boolean = false,
    val isLoading: Boolean = false
)
