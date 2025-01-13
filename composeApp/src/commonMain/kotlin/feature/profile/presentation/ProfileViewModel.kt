package feature.profile.presentation

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import feature.profile.ui.models.ProfileAction
import feature.profile.ui.models.ProfileEvent
import feature.profile.ui.models.ProfileViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Stable
class ProfileViewModel : ViewModel() {
    private val _viewStates = MutableStateFlow(ProfileViewState())
    private val _viewActions = MutableStateFlow<ProfileAction?>(null)

    fun viewStates(): StateFlow<ProfileViewState> = _viewStates.asStateFlow()
    fun viewActions(): Flow<ProfileAction?> = _viewActions.asStateFlow()

    fun obtainEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.LoadProfile -> loadProfile()
            ProfileEvent.OpenSettings -> openSettings()
        }
    }

    fun clearAction() {
        _viewActions.update { null }
    }

    private fun loadProfile() {
        // TODO: Implement profile loading logic
        _viewStates.update { 
            it.copy(
                name = "John Doe",
                email = "john.doe@example.com"
            )
        }
    }

    private fun openSettings() {
        _viewActions.update { ProfileAction.NavigateToSettings }
    }
} 