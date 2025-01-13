package feature.profile.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import feature.profile.ui.models.ProfileEvent
import feature.profile.ui.models.ProfileViewState
import org.jetbrains.compose.resources.stringResource
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.title_profile
import tech.mobiledeveloper.jethabit.resources.title_settings
import ui.themes.JetHabitTheme
import ui.themes.components.JHDivider

@Composable
internal fun ProfileView(
    viewState: ProfileViewState,
    eventHandler: (ProfileEvent) -> Unit
) {
    Surface(
        color = JetHabitTheme.colors.primaryBackground,
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {
            TopAppBar(
                backgroundColor = JetHabitTheme.colors.primaryBackground,
                elevation = 8.dp
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = JetHabitTheme.shapes.padding),
                    text = stringResource(Res.string.title_profile),
                    color = JetHabitTheme.colors.primaryText,
                    style = JetHabitTheme.typography.toolbar
                )
            }

            if (viewState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = JetHabitTheme.colors.tintColor)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(JetHabitTheme.shapes.padding)
                ) {
                    Text(
                        text = viewState.name,
                        style = JetHabitTheme.typography.heading,
                        color = JetHabitTheme.colors.primaryText
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = viewState.email,
                        style = JetHabitTheme.typography.body,
                        color = JetHabitTheme.colors.secondaryText
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Menu Section
                    Text(
                        text = "Menu",
                        style = JetHabitTheme.typography.heading,
                        color = JetHabitTheme.colors.primaryText
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Settings Menu Item
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { eventHandler(ProfileEvent.OpenSettings) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = JetHabitTheme.colors.primaryText
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = stringResource(Res.string.title_settings),
                            style = JetHabitTheme.typography.body,
                            color = JetHabitTheme.colors.primaryText
                        )
                    }
                    
                    JHDivider()
                }
            }
        }
    }
} 