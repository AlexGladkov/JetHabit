package feature.projects.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import feature.projects.presentation.ProjectListComponent

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun ProjectListScreen(
    component: ProjectListComponent
) {
    val viewState by component.state.subscribeAsState()

    ProjectListView(
        viewState = viewState,
        eventHandler = component::onEvent
    )
}
