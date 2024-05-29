package ui.themes.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.themes.JetHabitTheme

@Composable
fun JHDivider() = Divider(modifier = Modifier.padding(start = 16.dp).height(1.dp).fillMaxWidth(),
    thickness = 0.5.dp, color = JetHabitTheme.colors.controlColor.copy(alpha = 0.1f))