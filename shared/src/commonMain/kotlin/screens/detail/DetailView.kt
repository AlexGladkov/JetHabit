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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import di.LocalPlatform
import di.Platform
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.alexgladkov.odyssey.compose.extensions.present
import ru.alexgladkov.odyssey.compose.local.LocalRootController
import ru.alexgladkov.odyssey.compose.navigation.modal_navigation.ModalSheetConfiguration
import screens.detail.models.DetailViewState
import screens.settings.views.MenuItem
import screens.settings.views.MenuItemModel
import tech.mobiledeveloper.shared.AppRes
import ui.themes.JetHabitSize
import ui.themes.JetHabitTheme
import ui.themes.components.CCalendar
import ui.themes.components.JetMenu

@Composable
internal fun DetailView(
    viewState: DetailViewState,
    onCloseClicked: () -> Unit,
    onDeleteItemClicked: () -> Unit,
    onStartDateSelected: (Instant) -> Unit,
    onEndDateSelected: (Instant) -> Unit
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
                        selectedDate = Clock.System.now(),
                        textColor = JetHabitTheme.colors.primaryText,
                        dayOfWeekColor = JetHabitTheme.colors.controlColor,
                        selectedColor = JetHabitTheme.colors.tintColor
                    ) {
                        onStartDateSelected.invoke(it)
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
                        selectedDate = Clock.System.now(),
                        textColor = JetHabitTheme.colors.primaryText,
                        dayOfWeekColor = JetHabitTheme.colors.controlColor,
                        selectedColor = JetHabitTheme.colors.tintColor
                    ) {
                        onEndDateSelected.invoke(it)
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