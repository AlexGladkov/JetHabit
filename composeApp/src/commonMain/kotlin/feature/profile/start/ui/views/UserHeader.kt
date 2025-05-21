package feature.profile.start.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import feature.profile.start.ui.models.ProfileViewState
import ui.themes.JetHabitTheme

@Composable
internal fun UserHeader(viewState: ProfileViewState) {
    Column {
        Box(
            modifier = Modifier.padding(16.dp)
                .size(128.dp)
                .clip(RoundedCornerShape(64.dp))
                .background(JetHabitTheme.colors.secondaryBackground)
        )

        Text(
            modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            text = viewState.name.ifBlank { "Add username" },
            style = JetHabitTheme.typography.heading,
            color = JetHabitTheme.colors.primaryText
        )
        Text(
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, top = 4.dp),
            text = viewState.email.ifBlank { "Add email" },
            style = JetHabitTheme.typography.body,
            color = JetHabitTheme.colors.primaryText
        )
    }
}