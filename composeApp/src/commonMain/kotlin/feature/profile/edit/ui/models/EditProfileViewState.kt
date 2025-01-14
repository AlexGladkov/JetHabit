package feature.profile.edit.ui.models

import androidx.compose.runtime.Stable

@Stable
data class EditProfileViewState(
    val name: String = "",
    val email: String = "",
    val isEmailValid: Boolean = true,
    val showEmailError: Boolean = false,
    val isSaving: Boolean = false,
    val isLoading: Boolean = false
) 