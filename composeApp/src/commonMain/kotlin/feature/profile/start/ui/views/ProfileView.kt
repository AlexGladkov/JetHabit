package feature.profile.start.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import feature.profile.start.ui.models.ProfileEvent
import feature.profile.start.ui.models.ProfileViewState
import ui.components.AppHeader
import ui.themes.JetHabitTheme
import ui.themes.components.JetHabitButton

@Composable
internal fun ProfileView(
    viewState: ProfileViewState,
    eventHandler: (ProfileEvent) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = JetHabitTheme.colors.primaryBackground
    ) {
        Column {
            AppHeader(title = "Profile")

            UserHeader(viewState = viewState)

            JetHabitButton(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                text = "Edit Profile",
                backgroundColor = JetHabitTheme.colors.secondaryBackground,
                onClick = {
                    eventHandler.invoke(ProfileEvent.EditProfileClicked)
                }
            )

            MenuItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = JetHabitTheme.colors.primaryText
                    )
                },
                title = "Settings",
                onClick = { eventHandler(ProfileEvent.OpenSettings) }
            )
            Divider(
                color = JetHabitTheme.colors.primaryBackground,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

private fun ProfileViewState.isProfileEmpty(): Boolean {
    return name.isBlank() && email.isBlank() && avatarUrl == null
} 