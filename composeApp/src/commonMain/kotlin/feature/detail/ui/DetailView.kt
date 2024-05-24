package screens.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import di.LocalPlatform
import di.Platform
import feature.detail.presentation.models.DetailEvent
import screens.detail.models.DetailViewState
import tech.mobiledeveloper.jethabit.app.AppRes
import ui.themes.JetHabitTheme
import ui.themes.components.JetMenu

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun DetailView(
    viewState: DetailViewState,
    eventHandler: (DetailEvent) -> Unit
) {
    val platform = LocalPlatform.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = JetHabitTheme.colors.primaryBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (platform == Platform.iOS) {
                    Icon(
                        modifier = Modifier
                            .clickable { eventHandler.invoke(DetailEvent.CloseScreen) }
                            .size(56.dp).padding(16.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = JetHabitTheme.colors.controlColor
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
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

                Icon(
                    modifier = Modifier.clickable { eventHandler.invoke(DetailEvent.DeleteItem) }
                        .size(56.dp).padding(16.dp),
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Item",
                    tint = JetHabitTheme.colors.controlColor)
            }

            Spacer(modifier = Modifier.height(24.dp))

            JetMenu(
                title = AppRes.string.title_start_date,
                value = viewState.startDate
            ) {
                eventHandler.invoke(DetailEvent.StartDateClicked)
            }

            JetMenu(
                title = AppRes.string.title_end_date,
                value = viewState.endDate
            ) {
                eventHandler.invoke(DetailEvent.EndDateClicked)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
                    .height(48.dp)
                    .fillMaxWidth(),
                onClick = { eventHandler.invoke(DetailEvent.SaveChanges) },
                enabled = !viewState.isDeleting,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = JetHabitTheme.colors.tintColor,
                    disabledBackgroundColor = JetHabitTheme.colors.tintColor.copy(
                        alpha = 0.3f
                    )
                )
            ) {
                Text(
                    text = AppRes.string.action_save,
                    style = JetHabitTheme.typography.body,
                    color = Color.White
                )
            }
        }
    }
}