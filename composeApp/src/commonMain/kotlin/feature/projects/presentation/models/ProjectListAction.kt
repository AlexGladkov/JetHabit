package feature.projects.presentation.models

sealed class ProjectListAction {
    data object NavigateBack : ProjectListAction()
}
