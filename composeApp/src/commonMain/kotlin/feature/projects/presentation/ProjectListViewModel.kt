package feature.projects.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import di.Inject
import feature.projects.domain.CreateProjectUseCase
import feature.projects.domain.DeleteProjectUseCase
import feature.projects.domain.GetAllProjectsUseCase
import feature.projects.domain.UpdateProjectUseCase
import feature.projects.presentation.models.ProjectListAction
import feature.projects.presentation.models.ProjectListEvent
import feature.projects.presentation.models.ProjectListState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProjectListViewModel : BaseViewModel<ProjectListState, ProjectListAction, ProjectListEvent>(
    initialState = ProjectListState()
) {

    private val getAllProjectsUseCase = Inject.instance<GetAllProjectsUseCase>()
    private val createProjectUseCase = Inject.instance<CreateProjectUseCase>()
    private val updateProjectUseCase = Inject.instance<UpdateProjectUseCase>()
    private val deleteProjectUseCase = Inject.instance<DeleteProjectUseCase>()

    init {
        loadProjects()
    }

    override fun obtainEvent(viewEvent: ProjectListEvent) {
        when (viewEvent) {
            ProjectListEvent.BackClicked -> viewAction = ProjectListAction.NavigateBack
            ProjectListEvent.AddProjectClicked -> openDialogForCreate()
            is ProjectListEvent.ProjectClicked -> openDialogForEdit(viewEvent.projectId)
            is ProjectListEvent.CreateProject -> createProject(viewEvent.title, viewEvent.colorHex)
            is ProjectListEvent.UpdateProject -> updateProject(viewEvent.id, viewEvent.title, viewEvent.colorHex)
            is ProjectListEvent.DeleteProject -> deleteProject(viewEvent.projectId)
            ProjectListEvent.CloseDialog -> closeDialog()
        }
    }

    private fun loadProjects() {
        viewModelScope.launch(Dispatchers.Default) {
            viewState = viewState.copy(isLoading = true)
            val projects = getAllProjectsUseCase.execute()
            viewState = viewState.copy(projects = projects, isLoading = false)
        }
    }

    private fun openDialogForCreate() {
        viewState = viewState.copy(showEditDialog = true, editingProject = null)
    }

    private fun openDialogForEdit(projectId: String) {
        val project = viewState.projects.find { it.id == projectId }
        viewState = viewState.copy(showEditDialog = true, editingProject = project)
    }

    private fun closeDialog() {
        viewState = viewState.copy(showEditDialog = false, editingProject = null)
    }

    private fun createProject(title: String, colorHex: String) {
        viewModelScope.launch(Dispatchers.Default) {
            createProjectUseCase.execute(title, colorHex)
            loadProjects()
            closeDialog()
        }
    }

    private fun updateProject(id: String, title: String, colorHex: String) {
        viewModelScope.launch(Dispatchers.Default) {
            updateProjectUseCase.execute(id, title, colorHex)
            loadProjects()
            closeDialog()
        }
    }

    private fun deleteProject(projectId: String) {
        viewModelScope.launch(Dispatchers.Default) {
            deleteProjectUseCase.execute(projectId)
            loadProjects()
            closeDialog()
        }
    }
}
