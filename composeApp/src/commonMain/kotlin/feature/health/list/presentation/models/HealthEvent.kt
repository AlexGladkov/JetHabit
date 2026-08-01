package feature.health.list.presentation.models

sealed interface HealthEvent {
    data class ProjectSelected(
        val projectId: String?,
        val isUncategorizedSelected: Boolean = false
    ) : HealthEvent
}
