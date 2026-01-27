package feature.profile.start.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import di.Inject
import feature.profile.start.presentation.ProfileViewModel
import feature.profile.start.ui.models.ProfileAction
import feature.profile.start.ui.views.ProfileView
import navigation.LocalNavHost

@Composable
internal fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel { 
        ProfileViewModel(
            imagePicker = Inject.instance(),
            userProfileDao = Inject.instance()
        )
    }
) {
    val outerNavController = LocalNavHost.current
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(null)

    ProfileView(viewState = viewState) {
        viewModel.obtainEvent(it)
    }

    when (viewAction) {
        ProfileAction.NavigateToEdit -> {
            navController.navigate("Edit")
            viewModel.clearAction()
        }
        ProfileAction.NavigateToSettings -> {
            navController.navigate("Settings")
            viewModel.clearAction()
        }
        ProfileAction.NavigateToProjects -> {
            navController.navigate("Projects")
            viewModel.clearAction()
        }
        null -> {}
    }
} 