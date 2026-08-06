package feature.profile.start.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import core.auth.AuthRepository
import core.auth.AuthResult
import core.database.dao.UserProfileDao
import core.platform.ImagePicker
import feature.profile.start.ui.models.ProfileEvent
import feature.profile.start.ui.models.ProfileAction
import feature.profile.start.ui.models.ProfileViewState
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val imagePicker: ImagePicker,
    private val userProfileDao: UserProfileDao,
    private val authRepository: AuthRepository
) : BaseViewModel<ProfileViewState, ProfileAction, ProfileEvent>(ProfileViewState()) {

    init {
        obtainEvent(ProfileEvent.LoadProfile)
    }

    override fun obtainEvent(viewEvent: ProfileEvent) {
        when (viewEvent) {
            is ProfileEvent.PickImageFromLibrary -> pickImage()
            is ProfileEvent.TakePhoto -> takePhoto()
            ProfileEvent.EditProfileClicked -> {
                viewAction = ProfileAction.NavigateToEdit
            }
            ProfileEvent.OpenSettings -> {
                viewAction = ProfileAction.NavigateToSettings
            }
            ProfileEvent.OpenProjects -> {
                viewAction = ProfileAction.NavigateToProjects
            }
            ProfileEvent.LoadProfile -> loadProfile()
            ProfileEvent.VkLoginClicked -> performVkLogin()
            ProfileEvent.LogoutClicked -> logout()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            viewState = viewState.copy(isLoading = true)
            userProfileDao.getUserProfile().collect { profile ->
                viewState = viewState.copy(
                    name = profile?.name.orEmpty(),
                    email = profile?.email.orEmpty(),
                    avatarUrl = profile?.avatarUri,
                    isLoading = false,
                    isLoggedIn = profile?.authProvider != null,
                    authProvider = profile?.authProvider
                )
            }
        }
    }

    private fun performVkLogin() {
        viewModelScope.launch {
            val result = authRepository.signIn()
            handleLoginResult(result)
        }
    }

    private fun handleLoginResult(result: AuthResult) {
        when (result) {
            is AuthResult.Success -> {
                // AuthRepository already saved the profile
                // Profile will be updated via flow in loadProfile
            }
            is AuthResult.Error -> {
                viewAction = ProfileAction.ShowError(result.message)
            }
            AuthResult.Cancelled -> {
                // User cancelled, do nothing
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                // Profile will be updated via flow in loadProfile
            } catch (e: Exception) {
                viewAction = ProfileAction.ShowError(e.message ?: "Logout failed")
            }
        }
    }

    private fun pickImage() {
        viewModelScope.launch {
            val uri = imagePicker.pickImage()
            if (uri != null) {
                userProfileDao.updateAvatar(uri)
            }
        }
    }

    private fun takePhoto() {
        viewModelScope.launch {
            val uri = imagePicker.takePhoto()
            if (uri != null) {
                userProfileDao.updateAvatar(uri)
            }
        }
    }
} 