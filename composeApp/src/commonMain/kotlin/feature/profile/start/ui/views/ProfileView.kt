package feature.profile.start.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import di.LocalPlatform
import feature.profile.start.ui.models.ProfileEvent
import feature.profile.start.ui.models.ProfileViewState
import ui.components.PlatformImage
import ui.themes.JetHabitTheme

@Composable
internal fun ProfileView(
    viewState: ProfileViewState,
    eventHandler: (ProfileEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(JetHabitTheme.colors.primaryBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { eventHandler(ProfileEvent.EditProfileClicked) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = JetHabitTheme.colors.secondaryBackground
            ) {
                if (viewState.avatarUrl != null) {
                    PlatformImage(
                        url = viewState.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = JetHabitTheme.colors.primaryText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = viewState.name,
                    style = JetHabitTheme.typography.toolbar,
                    color = JetHabitTheme.colors.primaryText
                )
                Text(
                    text = viewState.email,
                    style = JetHabitTheme.typography.body,
                    color = JetHabitTheme.colors.secondaryText
                )
            }
        }

        // Menu Section
        Text(
            text = "Menu",
            style = JetHabitTheme.typography.body,
            color = JetHabitTheme.colors.primaryText,
            modifier = Modifier.padding(16.dp)
        )

        // Menu Items
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = JetHabitTheme.colors.primaryBackground,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { eventHandler(ProfileEvent.OpenSettings) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = JetHabitTheme.colors.primaryText
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Settings",
                        style = JetHabitTheme.typography.body,
                        color = JetHabitTheme.colors.primaryText
                    )
                }
            }
        }
    }
} 