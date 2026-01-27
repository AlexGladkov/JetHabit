package feature.projects.presentation.models

import feature.projects.data.ProjectEntity

data class ProjectListState(
    val projects: List<ProjectEntity> = emptyList(),
    val isLoading: Boolean = false,
    val editingProject: ProjectEntity? = null,
    val showEditDialog: Boolean = false
)
