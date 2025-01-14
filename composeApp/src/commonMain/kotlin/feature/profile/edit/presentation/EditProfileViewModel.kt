package feature.profile.edit.presentation

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import core.database.dao.UserProfileDao
import core.database.entity.UserProfile
import core.utils.isValidEmail
import feature.profile.edit.ui.models.EditProfileEvent
import feature.profile.edit.ui.models.EditProfileAction
import feature.profile.edit.ui.models.EditProfileViewState
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val userProfileDao: UserProfileDao
) : BaseViewModel<EditProfileViewState, EditProfileAction, EditProfileEvent>(
    initialState = EditProfileViewState()
) {
    init {
        loadProfile()
    }

    override fun obtainEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.NameChanged -> {
                viewState = viewState.copy(name = event.name)
            }
            is EditProfileEvent.EmailChanged -> {
                viewState = viewState.copy(
                    email = event.email,
                    isEmailValid = event.email.isValidEmail()
                )
            }
            EditProfileEvent.SaveClicked -> saveProfile()
            EditProfileEvent.BackClicked -> {
                viewAction = EditProfileAction.NavigateBack
            }
            EditProfileEvent.LoadProfile -> loadProfile()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            viewState = viewState.copy(isLoading = true)
            userProfileDao.getUserProfile().collect { profile ->
                profile?.let {
                    viewState = viewState.copy(
                        name = it.name,
                        email = it.email,
                        isEmailValid = it.email.isValidEmail(),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun saveProfile() {
        val currentState = viewState
        if (!currentState.isEmailValid) {
            viewState = viewState.copy(showEmailError = true)
            return
        }

        viewModelScope.launch {
            viewState = viewState.copy(isSaving = true)
            userProfileDao.insertOrUpdateProfile(
                UserProfile(
                    name = currentState.name,
                    email = currentState.email,
                    phoneNumber = "",  // TODO: Add phone number field
                    avatarUri = null   // TODO: Add avatar handling
                )
            )
            viewState = viewState.copy(isSaving = false)
            viewAction = EditProfileAction.NavigateBack
        }
    }
} 