package ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.themes.JetHabitTheme

@Composable
fun AppHeader(
    title: String,
    isCloseButtonAvailable: Boolean = false,
    isBackButtonAvailable: Boolean = false,
    backClicked: () -> Unit = { },
    closeClicked: () -> Unit = { }
) {
    Box(modifier = Modifier.fillMaxWidth().height(72.dp).padding(horizontal = 16.dp)) {
        if (isBackButtonAvailable) {
            Icon(
                modifier = Modifier.clickable { backClicked.invoke() }.align(Alignment.CenterStart),
                imageVector = Icons.Default.ArrowBack,
                tint = JetHabitTheme.colors.primaryText,
                contentDescription = "Back Button"
            )
        }

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = title, style = JetHabitTheme.typography.toolbar,
            color = JetHabitTheme.colors.primaryText
        )

        if (isCloseButtonAvailable) {
            Icon(
                modifier = Modifier.clickable { closeClicked.invoke() }.align(Alignment.CenterEnd),
                imageVector = Icons.Default.Close,
                tint = JetHabitTheme.colors.primaryText,
                contentDescription = "Close Button"
            )
        }
    }
}