package screens.add_dates

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adeo.kviewmodel.odyssey.StoredViewModel
import io.github.skeptick.libres.compose.painterResource
import kotlinx.datetime.Clock
import ru.alexgladkov.odyssey.compose.extensions.present
import ru.alexgladkov.odyssey.compose.local.LocalRootController
import ru.alexgladkov.odyssey.compose.navigation.modal_navigation.ModalConfiguration
import ru.alexgladkov.odyssey.compose.navigation.modal_navigation.ModalSheetConfiguration
import screens.add_dates.models.MedicationAddDateCountType
import screens.add_dates.models.MedicationAddDatesAction
import screens.add_dates.models.MedicationAddDatesEvent
import screens.add_name.models.MedicationAddNameEvent
import screens.detail.models.DetailEvent
import tech.mobiledeveloper.shared.AppRes
import ui.themes.JetHabitTheme
import ui.themes.components.CCalendar
import ui.themes.components.CounterModalSheet
import ui.themes.components.JetHabitButton

@Composable
fun MedicationAddDates() {

    StoredViewModel(factory = { MedicationAddDatesViewModel() }) { viewModel ->
        val viewState by viewModel.viewStates().collectAsState()
        val viewAction by viewModel.viewActions().collectAsState(null)

        Column(
            modifier = Modifier.fillMaxSize().background(JetHabitTheme.colors.secondaryBackground),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.padding(top = 56.dp).size(72.dp),
                painter = painterResource(AppRes.image.medication_calendar),
                contentDescription = "Medication Add Dates Icon"
            )

            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = AppRes.string.medication_add_dates,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                color = JetHabitTheme.colors.primaryText,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Column(
                modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth().clip(RoundedCornerShape(8.dp))
                    .background(JetHabitTheme.colors.primaryBackground)
                    .padding(vertical = 16.dp)
            ) {
                Row {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = AppRes.string.medication_periodicity,
                        color = JetHabitTheme.colors.primaryText,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        modifier = Modifier.clickable {
                            viewModel.obtainEvent(MedicationAddDatesEvent.PeriodicityClicked)
                        }.padding(horizontal = 16.dp),
                        text = "Every Day",
                        color = JetHabitTheme.colors.tintColor,
                        fontSize = 16.sp
                    )
                }

                Divider(
                    modifier = Modifier.padding(bottom = 12.dp, top = 12.dp, start = 16.dp)
                        .fillMaxWidth()
                        .background(JetHabitTheme.colors.controlColor.copy(alpha = 0.4f)),
                    thickness = 0.5.dp,
                )

                Row {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = AppRes.string.medication_frequency,
                        color = JetHabitTheme.colors.primaryText,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        modifier = Modifier.clickable {
                            viewModel.obtainEvent(MedicationAddDatesEvent.FrequencyClicked)
                        }.padding(horizontal = 16.dp),
                        text = viewState.frequency,
                        color = JetHabitTheme.colors.tintColor,
                        fontSize = 16.sp
                    )
                }
            }

            Text(
                modifier = Modifier.padding(top = 8.dp, start = 32.dp, end = 20.dp).fillMaxWidth(),
                text = AppRes.string.medication_dates_hint,
                fontSize = 10.sp,
                color = JetHabitTheme.colors.controlColor
            )

            Text(
                modifier = Modifier.padding(top = 32.dp, start = 32.dp).fillMaxWidth(),
                text = AppRes.string.medication_dates_duration,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = JetHabitTheme.colors.primaryText
            )

            Column(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth().clip(RoundedCornerShape(8.dp))
                    .background(JetHabitTheme.colors.primaryBackground)
                    .padding(vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (viewState.startDate == null) {
                        Image(
                            modifier = Modifier.padding(start = 16.dp).size(16.dp),
                            painter = painterResource(AppRes.image.ic_add),
                            contentDescription = "Add Start Date"
                        )

                        Text(
                            modifier = Modifier.clickable {
                                viewModel.obtainEvent(MedicationAddDatesEvent.AddStartDateClicked)
                            }.padding(horizontal = 16.dp),
                            text = AppRes.string.medication_add_start_date,
                            color = JetHabitTheme.colors.tintColor,
                            fontSize = 16.sp
                        )
                    } else {
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = ">",
                            color = JetHabitTheme.colors.primaryText,
                            fontSize = 16.sp
                        )

                        Text(
                            modifier = Modifier.clickable {
                                viewModel.obtainEvent(MedicationAddDatesEvent.AddStartDateClicked)
                            }.padding(horizontal = 16.dp),
                            text = viewState.startDate!!,
                            color = JetHabitTheme.colors.tintColor,
                            fontSize = 16.sp
                        )
                    }
                }

                Divider(
                    modifier = Modifier.padding(bottom = 12.dp, top = 12.dp, start = 16.dp)
                        .fillMaxWidth()
                        .background(JetHabitTheme.colors.controlColor.copy(alpha = 0.4f)),
                    thickness = 0.5.dp,
                )

                Row {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = AppRes.string.medication_week_count,
                        color = JetHabitTheme.colors.primaryText,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        modifier = Modifier.clickable {
                            viewModel.obtainEvent(MedicationAddDatesEvent.WeekCountClicked)
                        }.padding(horizontal = 16.dp),
                        text = viewState.weekCount,
                        color = JetHabitTheme.colors.tintColor,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            JetHabitButton(
                modifier = Modifier.padding(bottom = 40.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                text = AppRes.string.action_next,
                onClick = {

                })
        }

        val rootController = LocalRootController.current
        val modalController = rootController.findModalController()
        when (viewAction) {
            MedicationAddDatesAction.PresentStartDate -> {
                val modalConfiguration = ModalSheetConfiguration()
                modalController.present(modalConfiguration) { key ->
                    Box(
                        modifier = Modifier
                            .background(JetHabitTheme.colors.primaryBackground)
                            .padding(bottom = 20.dp)
                    ) {
                        CCalendar(
                            selectedDate = Clock.System.now(),
                            textColor = JetHabitTheme.colors.primaryText,
                            dayOfWeekColor = JetHabitTheme.colors.controlColor,
                            selectedColor = JetHabitTheme.colors.tintColor
                        ) {
                            viewModel.obtainEvent(MedicationAddDatesEvent.StarDateSelected(it))
                            modalController.popBackStack(key)
                        }
                    }
                }

                viewModel.obtainEvent(MedicationAddDatesEvent.ActionInvoked)
            }

            is MedicationAddDatesAction.PresentCountSelection -> {
                val modalConfiguration = ModalSheetConfiguration()
                val type = (viewAction as MedicationAddDatesAction.PresentCountSelection).medicationAddDateCountType
                modalController.present(modalConfiguration) { key ->
                    CounterModalSheet(
                        title = when (type) {
                            MedicationAddDateCountType.Frequency -> AppRes.string.medication_frequency
                            MedicationAddDateCountType.WeekCount -> AppRes.string.medication_week_count
                        },
                        count = when (type) {
                            MedicationAddDateCountType.Frequency -> viewState.frequency
                            MedicationAddDateCountType.WeekCount -> viewState.weekCount
                        },
                        onCountClicked = {
                            viewModel.obtainEvent(
                                MedicationAddDatesEvent.CountSelected(
                                    type = type,
                                    value = it
                                )
                            )

                            modalController.popBackStack(key)
                        },
                        onCloseClick = {
                            modalController.popBackStack(key)
                        }
                    )
                }

                viewModel.obtainEvent(MedicationAddDatesEvent.ActionInvoked)
            }

            null -> {}
        }
    }
}