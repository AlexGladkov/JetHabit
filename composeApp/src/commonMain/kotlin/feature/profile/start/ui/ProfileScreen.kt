package feature.profile.start.ui

import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import di.Inject
import feature.profile.start.presentation.ProfileViewModel
import feature.profile.start.ui.models.ProfileAction
import feature.profile.start.ui.views.ProfileView
import kotlinx.coroutines.launch
import navigation.LocalNavHost

@Composable
internal fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel {
        ProfileViewModel(
            imagePicker = Inject.instance(),
            userProfileDao = Inject.instance(),
            authRepository = Inject.instance()
        )
    }
) {
    val outerNavController = LocalNavHost.current
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(null)
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    ProfileView(
        viewState = viewState,
        scaffoldState = scaffoldState,
        eventHandler = { viewModel.obtainEvent(it) }
    )

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
        is ProfileAction.ShowError -> {
            LaunchedEffect(viewAction) {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = (viewAction as ProfileAction.ShowError).message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            viewModel.clearAction()
        }
        null -> {}
    }
} 