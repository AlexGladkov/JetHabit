package feature.profile.start.ui.models

import androidx.compose.runtime.Stable

@Stable
data class ProfileViewState(
    val name: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val authProvider: String? = null
) 