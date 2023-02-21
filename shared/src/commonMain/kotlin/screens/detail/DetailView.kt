package screens.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import di.LocalPlatform
import di.Platform
import screens.detail.models.DetailViewState
import tech.mobiledeveloper.JetHabit.AppRes
import ui.themes.JetHabitTheme

@Composable
internal fun DetailView(
    viewState: DetailViewState,
    onCloseClicked: () -> Unit,
    onDeleteItemClicked: () -> Unit
) {
    val platform = LocalPlatform.current

    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (platform == Platform.iOS) {
                Icon(
                    modifier = Modifier
                        .clickable { onCloseClicked() }
                        .size(56.dp).padding(16.dp),
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = JetHabitTheme.colors.controlColor
                )
            }

            Column {
                Text(
                    modifier = Modifier.padding(
                        start = JetHabitTheme.shapes.padding,
                        end = JetHabitTheme.shapes.padding,
                    ),
                    text = viewState.itemTitle,
                    style = JetHabitTheme.typography.heading,
                    color = JetHabitTheme.colors.primaryText
                )

                Text(
                    modifier = Modifier.padding(
                        start = JetHabitTheme.shapes.padding,
                        end = JetHabitTheme.shapes.padding,
                        top = 2.dp
                    ),
                    text = if (viewState.isGood) "Good Habit" else "Bad Habit",
                    style = JetHabitTheme.typography.caption,
                    color = JetHabitTheme.colors.controlColor
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier
                .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
                .height(48.dp)
                .fillMaxWidth(),
            onClick = onDeleteItemClicked,
            enabled = !viewState.isDeleting,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = JetHabitTheme.colors.errorColor,
                disabledBackgroundColor = JetHabitTheme.colors.errorColor.copy(
                    alpha = 0.3f
                )
            )
        ) {
            if (viewState.isDeleting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = AppRes.string.action_delete_item,
                    style = JetHabitTheme.typography.body,
                    color = Color.White
                )
            }
        }
    }
}