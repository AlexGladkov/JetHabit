package feature.projects.presentation.models

sealed class ProjectListEvent {
    data object BackClicked : ProjectListEvent()
    data object AddProjectClicked : ProjectListEvent()
    data class ProjectClicked(val projectId: String) : ProjectListEvent()
    data class CreateProject(val title: String, val colorHex: String) : ProjectListEvent()
    data class UpdateProject(val id: String, val title: String, val colorHex: String) : ProjectListEvent()
    data class DeleteProject(val projectId: String) : ProjectListEvent()
    data object CloseDialog : ProjectListEvent()
}
