package feature.projects.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import feature.projects.presentation.models.ProjectListEvent
import feature.projects.presentation.models.ProjectListState
import feature.projects.ui.views.ProjectCard
import feature.projects.ui.views.ProjectEditDialog
import ui.components.AppHeader

@Composable
fun ProjectListView(
    viewState: ProjectListState,
    eventHandler: (ProjectListEvent) -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                title = "My Projects",
                onBackClick = { eventHandler(ProjectListEvent.BackClicked) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { eventHandler(ProjectListEvent.AddProjectClicked) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Project")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (viewState.isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewState.projects) { project ->
                        ProjectCard(
                            project = project,
                            onClick = { eventHandler(ProjectListEvent.ProjectClicked(project.id)) }
                        )
                    }
                }
            }

            if (viewState.showEditDialog) {
                ProjectEditDialog(
                    project = viewState.editingProject,
                    onDismiss = { eventHandler(ProjectListEvent.CloseDialog) },
                    onSave = { title, colorHex ->
                        if (viewState.editingProject != null) {
                            eventHandler(
                                ProjectListEvent.UpdateProject(
                                    viewState.editingProject.id,
                                    title,
                                    colorHex
                                )
                            )
                        } else {
                            eventHandler(ProjectListEvent.CreateProject(title, colorHex))
                        }
                    },
                    onDelete = {
                        viewState.editingProject?.let { project ->
                            eventHandler(ProjectListEvent.DeleteProject(project.id))
                        }
                    }
                )
            }
        }
    }
}
