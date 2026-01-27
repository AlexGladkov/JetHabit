package feature.projects.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import feature.projects.presentation.ProjectListViewModel
import feature.projects.presentation.models.ProjectListAction

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun ProjectListScreen(
    navController: NavController,
    viewModel: ProjectListViewModel = viewModel { ProjectListViewModel() }
) {
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(null)

    ProjectListView(
        viewState = viewState,
        eventHandler = viewModel::obtainEvent
    )

    when (viewAction) {
        ProjectListAction.NavigateBack -> {
            navController.popBackStack()
            viewModel.clearAction()
        }
        null -> {}
    }
}
