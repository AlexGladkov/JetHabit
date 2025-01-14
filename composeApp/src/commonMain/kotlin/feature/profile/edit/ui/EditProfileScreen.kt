package feature.profile.edit.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import di.Inject
import feature.profile.edit.presentation.EditProfileViewModel
import feature.profile.edit.ui.models.EditProfileAction
import feature.profile.edit.ui.models.EditProfileEvent

@Composable
internal fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = viewModel { EditProfileViewModel(Inject.instance()) }
) {
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(null)

    EditProfileView(viewState = viewState) {
        viewModel.obtainEvent(it)
    }

    when (viewAction) {
        EditProfileAction.NavigateBack -> {
            navController.popBackStack()
            viewModel.clearAction()
        }
        null -> {}
    }

    LaunchedEffect(Unit) {
        viewModel.obtainEvent(EditProfileEvent.LoadProfile)
    }
} 