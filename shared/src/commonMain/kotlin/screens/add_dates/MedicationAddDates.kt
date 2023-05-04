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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
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
import ru.alexgladkov.odyssey.compose.extensions.present
import ru.alexgladkov.odyssey.compose.local.LocalRootController
import ru.alexgladkov.odyssey.compose.navigation.modal_navigation.ModalSheetConfiguration
import screens.add_dates.models.MedicationAddDatesAction
import screens.add_dates.models.MedicationAddDatesEvent
import tech.mobiledeveloper.shared.AppRes
import ui.themes.JetHabitTheme
import ui.themes.components.CCalendar
import ui.themes.components.CounterModalSheet
import ui.themes.components.JetHabitButton
import ui.themes.components.TimesOfDaySelectionSheet
import ui.themes.components.WeekSelectionSheet

@Composable
internal fun MedicationAddDates(title: String) {

    StoredViewModel(factory = { MedicationAddDatesViewModel(title) }) { viewModel ->
        val viewState by viewModel.viewStates().collectAsState()
        val viewAction by viewModel.viewActions().collectAsState(null)
        val rootController = LocalRootController.current

        Column(
            modifier = Modifier.fillMaxSize().background(JetHabitTheme.colors.secondaryBackground),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text(
                    modifier = Modifier
                        .clickable { rootController.popBackStack() }
                        .align(Alignment.CenterStart)
                        .padding(horizontal = 20.dp),
                    text = AppRes.string.action_back,
                    color = JetHabitTheme.colors.tintColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light
                )

                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = viewState.name,
                    color = JetHabitTheme.colors.primaryText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Image(
                modifier = Modifier.padding(top = 32.dp).size(72.dp),
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
            ) {
                Row(
                    modifier = Modifier.clickable {
                        viewModel.obtainEvent(MedicationAddDatesEvent.PeriodicityClicked)
                    }.padding(all = 16.dp)
                ) {
                    Text(
                        text = AppRes.string.medication_periodicity,
                        color = JetHabitTheme.colors.primaryText,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = viewState.periodicity,
                        color = JetHabitTheme.colors.tintColor,
                        fontSize = 16.sp
                    )
                }


                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(JetHabitTheme.colors.controlColor.copy(alpha = 0.4f)),
                    thickness = 0.5.dp,
                )

                Row(
                    modifier = Modifier.clickable {
                        viewModel.obtainEvent(MedicationAddDatesEvent.FrequencyClicked)
                    }.padding(all = 16.dp)
                ) {
                    Text(
                        text = AppRes.string.medication_frequency,
                        color = JetHabitTheme.colors.primaryText,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
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
            ) {
                Row(
                    modifier = Modifier.clickable {
                        viewModel.obtainEvent(MedicationAddDatesEvent.AddStartDateClicked)
                    }.padding(all = 16.dp).fillMaxWidth(),

                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (viewState.startDate == null) {
                        Image(
                            modifier = Modifier.padding(end = 16.dp).size(16.dp),
                            painter = painterResource(AppRes.image.ic_add),
                            contentDescription = "Add Start Date"
                        )

                        Text(
                            text = AppRes.string.medication_add_start_date,
                            color = JetHabitTheme.colors.tintColor,
                            fontSize = 16.sp
                        )
                    } else {
                        Text(
                            modifier = Modifier.padding(end = 16.dp),
                            text = ">",
                            color = JetHabitTheme.colors.primaryText,
                            fontSize = 16.sp
                        )

                        Text(
                            text = viewState.startDate!!,
                            color = JetHabitTheme.colors.tintColor,
                            fontSize = 16.sp
                        )
                    }
                }

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(JetHabitTheme.colors.controlColor.copy(alpha = 0.4f)),
                    thickness = 0.5.dp,
                )

                Row(
                    Modifier.clickable {
                        viewModel.obtainEvent(MedicationAddDatesEvent.WeekCountClicked)
                    }.padding(all = 16.dp)
                ) {
                    Text(
                        text = AppRes.string.medication_week_count,
                        color = JetHabitTheme.colors.primaryText,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
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
                text = AppRes.string.action_add,
                onClick = {
                    viewModel.obtainEvent(MedicationAddDatesEvent.AddNewMedicine)
                })
        }

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
                            selectedDate = viewState.calendarDate,
                            textColor = JetHabitTheme.colors.primaryText,
                            dayOfWeekColor = JetHabitTheme.colors.controlColor,
                            selectedColor = JetHabitTheme.colors.tintColor,
                            allowSameDate = true
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
                val type =
                    (viewAction as MedicationAddDatesAction.PresentCountSelection).medicationAddDateCountType
                modalController.present(modalConfiguration) { key ->
                    CounterModalSheet(
                        title = AppRes.string.medication_week_count,
                        count = viewState.weekCount,
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

            MedicationAddDatesAction.PresentPeriodicity -> {
                val modalConfiguration = ModalSheetConfiguration()
                modalController.present(modalConfiguration) { key ->
                    WeekSelectionSheet(onSaveClicked = {
                        viewModel.obtainEvent(MedicationAddDatesEvent.PeriodicitySelected(it))
                        modalController.popBackStack(key)
                    }, onCloseClick = {
                        modalController.popBackStack(key)
                    }, currentState = viewState.periodicityValues)
                }

                viewModel.obtainEvent(MedicationAddDatesEvent.ActionInvoked)
            }

            is MedicationAddDatesAction.PresentFrequency -> {
                val modalConfiguration = ModalSheetConfiguration()

                modalController.present(modalConfiguration) { key ->
                    TimesOfDaySelectionSheet(onSaveClicked = {
                        viewModel.obtainEvent(MedicationAddDatesEvent.FrequencySelected(it))
                        modalController.popBackStack(key)
                    }, onCloseClick = {
                        modalController.popBackStack(key)
                    }, currentState = viewState.frequencyValues)
                }

                viewModel.obtainEvent(MedicationAddDatesEvent.ActionInvoked)
            }

            MedicationAddDatesAction.CloseScreen -> rootController.findRootController()
                .backToScreen("main")

            null -> {}
        }
    }
}