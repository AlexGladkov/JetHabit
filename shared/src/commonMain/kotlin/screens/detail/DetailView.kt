package screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.soywiz.klock.DateTime
import di.LocalPlatform
import di.Platform
import ru.alexgladkov.odyssey.compose.extensions.present
import ru.alexgladkov.odyssey.compose.local.LocalRootController
import ru.alexgladkov.odyssey.compose.navigation.modal_navigation.ModalSheetConfiguration
import screens.detail.models.DetailEvent
import screens.detail.models.DetailViewState
import tech.mobiledeveloper.shared.AppRes
import ui.themes.JetHabitTheme
import ui.themes.components.CCalendar
import ui.themes.components.JetMenu

@Composable
internal fun DetailView(
    viewState: DetailViewState,
    eventHandler: (DetailEvent) -> Unit
) {
    val platform = LocalPlatform.current
    val rootController = LocalRootController.current
    val modalController = rootController.findModalController()

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
                    imageVector = Icons.Filled.ArrowBack,
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
            val configuration = ModalSheetConfiguration(
                maxHeight = 0.6f,
                cornerRadius = 16,
            )
            modalController.present(configuration) { key ->
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(JetHabitTheme.colors.primaryBackground)
                        .padding(16.dp)
                ) {
                    CCalendar(
                        selectedDate = viewState.start ?: DateTime.now(),
                        textColor = JetHabitTheme.colors.primaryText,
                        dayOfWeekColor = JetHabitTheme.colors.controlColor,
                        selectedColor = JetHabitTheme.colors.tintColor
                    ) {
                        eventHandler.invoke(DetailEvent.StartDateSelected(it))
                        modalController.popBackStack(key)
                    }
                }
            }
        }

        JetMenu(
            title = AppRes.string.title_end_date,
            value = viewState.endDate
        ) {
            val configuration = ModalSheetConfiguration(
                maxHeight = 0.6f,
                cornerRadius = 16,
            )
            modalController.present(configuration) { key ->
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(JetHabitTheme.colors.primaryBackground)
                        .padding(16.dp)
                ) {
                    CCalendar(
                        selectedDate = viewState.end ?: DateTime.now(),
                        textColor = JetHabitTheme.colors.primaryText,
                        dayOfWeekColor = JetHabitTheme.colors.controlColor,
                        selectedColor = JetHabitTheme.colors.tintColor
                    ) {
                        eventHandler.invoke(DetailEvent.EndDateSelected(it))
                        modalController.popBackStack(key)
                    }
                }
            }
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