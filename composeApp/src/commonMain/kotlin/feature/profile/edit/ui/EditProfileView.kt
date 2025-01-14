package feature.profile.edit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import feature.profile.edit.ui.models.EditProfileEvent
import feature.profile.edit.ui.models.EditProfileViewState
import ui.themes.JetHabitTheme

@Composable
fun EditProfileView(
    viewState: EditProfileViewState,
    eventHandler: (EditProfileEvent) -> Unit
) {
    Surface(
        color = JetHabitTheme.colors.primaryBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { eventHandler(EditProfileEvent.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                backgroundColor = JetHabitTheme.colors.primaryBackground,
                contentColor = JetHabitTheme.colors.primaryText
            )

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
                    // Name Field
                    Text(
                        text = "Name",
                        style = JetHabitTheme.typography.body,
                        color = JetHabitTheme.colors.primaryText
                    )
                    TextField(
                        value = viewState.name,
                        onValueChange = { eventHandler(EditProfileEvent.NameChanged(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = JetHabitTheme.colors.primaryBackground,
                            textColor = JetHabitTheme.colors.primaryText,
                            cursorColor = JetHabitTheme.colors.tintColor,
                            focusedIndicatorColor = JetHabitTheme.colors.tintColor
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Field
                    Text(
                        text = "Email",
                        style = JetHabitTheme.typography.body,
                        color = JetHabitTheme.colors.primaryText
                    )
                    TextField(
                        value = viewState.email,
                        onValueChange = { eventHandler(EditProfileEvent.EmailChanged(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = JetHabitTheme.colors.primaryBackground,
                            textColor = JetHabitTheme.colors.primaryText,
                            cursorColor = JetHabitTheme.colors.tintColor,
                            focusedIndicatorColor = JetHabitTheme.colors.tintColor
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Save Button
                    Button(
                        onClick = { eventHandler(EditProfileEvent.SaveClicked) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = !viewState.isSaving,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = JetHabitTheme.colors.tintColor,
                            disabledBackgroundColor = JetHabitTheme.colors.tintColor.copy(alpha = 0.3f)
                        )
                    ) {
                        if (viewState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Save",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
} 