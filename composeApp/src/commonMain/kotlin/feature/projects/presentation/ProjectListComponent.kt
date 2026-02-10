package feature.projects.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import feature.projects.domain.CreateProjectUseCase
import feature.projects.domain.DeleteProjectUseCase
import feature.projects.domain.GetAllProjectsUseCase
import feature.projects.domain.UpdateProjectUseCase
import feature.projects.presentation.models.ProjectListEvent
import feature.projects.presentation.models.ProjectListState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class ProjectListComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onNavigateBack: () -> Unit
) : ComponentContext by componentContext, DIAware {

    private val getAllProjectsUseCase: GetAllProjectsUseCase by di.instance()
    private val createProjectUseCase: CreateProjectUseCase by di.instance()
    private val updateProjectUseCase: UpdateProjectUseCase by di.instance()
    private val deleteProjectUseCase: DeleteProjectUseCase by di.instance()

    private val scope = coroutineScope(Dispatchers.Main)

    private val _state = MutableValue(ProjectListState())
    val state: Value<ProjectListState> = _state

    init {
        loadProjects()
    }

    fun onEvent(viewEvent: ProjectListEvent) {
        when (viewEvent) {
            ProjectListEvent.BackClicked -> onNavigateBack()
            ProjectListEvent.AddProjectClicked -> openDialogForCreate()
            is ProjectListEvent.ProjectClicked -> openDialogForEdit(viewEvent.projectId)
            is ProjectListEvent.CreateProject -> createProject(viewEvent.title, viewEvent.colorHex)
            is ProjectListEvent.UpdateProject -> updateProject(viewEvent.id, viewEvent.title, viewEvent.colorHex)
            is ProjectListEvent.DeleteProject -> deleteProject(viewEvent.projectId)
            ProjectListEvent.CloseDialog -> closeDialog()
        }
    }

    private fun loadProjects() {
        scope.launch(Dispatchers.Default) {
            _state.value = _state.value.copy(isLoading = true)
            val projects = getAllProjectsUseCase.execute()
            _state.value = _state.value.copy(projects = projects, isLoading = false)
        }
    }

    private fun openDialogForCreate() {
        _state.value = _state.value.copy(showEditDialog = true, editingProject = null)
    }

    private fun openDialogForEdit(projectId: String) {
        val project = _state.value.projects.find { it.id == projectId }
        _state.value = _state.value.copy(showEditDialog = true, editingProject = project)
    }

    private fun closeDialog() {
        _state.value = _state.value.copy(showEditDialog = false, editingProject = null)
    }

    private fun createProject(title: String, colorHex: String) {
        scope.launch(Dispatchers.Default) {
            createProjectUseCase.execute(title, colorHex)
            loadProjects()
            closeDialog()
        }
    }

    private fun updateProject(id: String, title: String, colorHex: String) {
        scope.launch(Dispatchers.Default) {
            updateProjectUseCase.execute(id, title, colorHex)
            loadProjects()
            closeDialog()
        }
    }

    private fun deleteProject(projectId: String) {
        scope.launch(Dispatchers.Default) {
            deleteProjectUseCase.execute(projectId)
            loadProjects()
            closeDialog()
        }
    }
}
