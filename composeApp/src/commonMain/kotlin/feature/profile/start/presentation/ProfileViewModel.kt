package feature.profile.start.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import core.database.dao.UserProfileDao
import core.platform.ImagePicker
import feature.profile.start.ui.models.ProfileEvent
import feature.profile.start.ui.models.ProfileAction
import feature.profile.start.ui.models.ProfileViewState
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val imagePicker: ImagePicker,
    private val userProfileDao: UserProfileDao
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
                    isLoading = false
                )
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