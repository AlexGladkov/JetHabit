package feature.profile.ui.models

import androidx.compose.runtime.Stable

@Stable
data class ProfileViewState(
    val name: String = "",
    val email: String = "",
    val isLoading: Boolean = false
) 