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
    scaffoldState: ScaffoldState,
    eventHandler: (ProfileEvent) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = JetHabitTheme.colors.primaryBackground
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = JetHabitTheme.colors.primaryBackground
        ) {
            Column {
                AppHeader(title = "Profile")

            // Show VK login button if not logged in and profile is empty
            if (!viewState.isLoggedIn && viewState.isProfileEmpty()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    VkLoginButton(
                        onClick = { eventHandler(ProfileEvent.VkLoginClicked) },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Or complete your profile manually",
                        style = JetHabitTheme.typography.caption,
                        color = JetHabitTheme.colors.secondaryText,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            UserHeader(viewState = viewState)

            JetHabitButton(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                text = "Edit Profile",
                backgroundColor = JetHabitTheme.colors.secondaryBackground,
                onClick = {
                    eventHandler.invoke(ProfileEvent.EditProfileClicked)
                }
            )

            // Show logout button if logged in via auth provider
            if (viewState.isLoggedIn && viewState.authProvider != null) {
                JetHabitButton(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                    text = "Logout",
                    backgroundColor = JetHabitTheme.colors.errorColor,
                    onClick = {
                        eventHandler.invoke(ProfileEvent.LogoutClicked)
                    }
                )
            }

            MenuItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = JetHabitTheme.colors.primaryText
                    )
                },
                title = "My Projects",
                onClick = { eventHandler(ProfileEvent.OpenProjects) }
            )
            Divider(
                color = JetHabitTheme.colors.primaryBackground,
                modifier = Modifier.padding(horizontal = 20.dp)
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
}

private fun ProfileViewState.isProfileEmpty(): Boolean {
    return name.isBlank() && email.isBlank() && avatarUrl == null
} 