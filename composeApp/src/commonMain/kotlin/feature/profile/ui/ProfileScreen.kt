package feature.profile.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import feature.profile.presentation.ProfileViewModel
import feature.profile.ui.models.ProfileAction
import feature.profile.ui.models.ProfileEvent
import feature.profile.ui.views.ProfileView
import navigation.AppScreens
import navigation.ProfileScreens

@ExperimentalFoundationApi
@Composable
internal fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(null)

    ProfileView(viewState = viewState) {
        viewModel.obtainEvent(it)
    }

    when (viewAction) {
        ProfileAction.NavigateToSettings -> {
            navController.navigate("${AppScreens.Profile.title}/${ProfileScreens.Settings.name}")
            viewModel.clearAction()
        }
        null -> {}
    }

    LaunchedEffect(Unit) {
        viewModel.obtainEvent(ProfileEvent.LoadProfile)
    }
} 