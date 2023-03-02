package ui.themes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.*
import tech.mobiledeveloper.shared.AppRes
import ui.themes.JetHabitTheme
import utils.daysInAMonth
import kotlin.time.Duration.Companion.days

@Composable
internal fun CCalendar(
    selectedDate: Instant,
    textColor: Color,
    dayOfWeekColor: Color,
    selectedColor: Color,
    cellSize: Dp = 40.dp,
    onDateSelected: (Instant) -> Unit
) {
    var dateState by remember { mutableStateOf(selectedDate) }
    val localDateTime by derivedStateOf { dateState.toLocalDateTime(TimeZone.currentSystemDefault()) }
    val monthOfSelectedDate = localDateTime.month.name.toLowerCase().capitalize()
    val yearOfSelectedDate = localDateTime.year

    Column {
        Row {
            Text(
                text = "$monthOfSelectedDate $yearOfSelectedDate",
                color = textColor,
                style = JetHabitTheme.typography.body,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                modifier = Modifier.clickable {
                    dateState = dateState.minus(1, DateTimeUnit.MONTH, TimeZone.currentSystemDefault())
                },
                imageVector = Icons.Filled.KeyboardArrowLeft,
                tint = JetHabitTheme.colors.tintColor,
                contentDescription = "Month Back"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier.clickable {
                    dateState = dateState.plus(1, DateTimeUnit.MONTH, TimeZone.currentSystemDefault())
                },
                imageVector = Icons.Filled.KeyboardArrowRight,
                tint = JetHabitTheme.colors.tintColor,
                contentDescription = "Month Forward"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                modifier = Modifier.weight(1f).height(cellSize),
                textAlign = TextAlign.Center,
                text = AppRes.string.days_monday_short, color = dayOfWeekColor,
                style = JetHabitTheme.typography.caption
            )
            Text(
                modifier = Modifier.weight(1f).height(cellSize),
                textAlign = TextAlign.Center,
                text = AppRes.string.days_tuesday_short, color = dayOfWeekColor,
                style = JetHabitTheme.typography.caption
            )
            Text(
                modifier = Modifier.weight(1f).height(cellSize),
                textAlign = TextAlign.Center,
                text = AppRes.string.days_wednesday_short, color = dayOfWeekColor,
                style = JetHabitTheme.typography.caption
            )
            Text(
                modifier = Modifier.weight(1f).height(cellSize),
                textAlign = TextAlign.Center,
                text = AppRes.string.days_thursday_short, color = dayOfWeekColor,
                style = JetHabitTheme.typography.caption
            )
            Text(
                modifier = Modifier.weight(1f).height(cellSize),
                textAlign = TextAlign.Center,
                text = AppRes.string.days_friday_short, color = dayOfWeekColor,
                style = JetHabitTheme.typography.caption
            )
            Text(
                modifier = Modifier.weight(1f).height(cellSize),
                textAlign = TextAlign.Center,
                text = AppRes.string.days_saturday_short, color = dayOfWeekColor,
                style = JetHabitTheme.typography.caption
            )
            Text(
                modifier = Modifier.weight(1f).height(cellSize),
                textAlign = TextAlign.Center,
                text = AppRes.string.days_sunday_short, color = dayOfWeekColor,
                style = JetHabitTheme.typography.caption
            )
        }

        DatesForMonth(textColor, dateState, localDateTime, selectedDate, cellSize, selectedColor) {
            val chosenDate = if (localDateTime.dayOfMonth < it)
                dateState.plus(it - localDateTime.dayOfMonth, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
            else
                dateState.minus(localDateTime.dayOfMonth - it, DateTimeUnit.DAY, TimeZone.currentSystemDefault())

            onDateSelected.invoke(chosenDate)
        }
    }
}

@Composable
internal fun DatesForMonth(
    textColor: Color,
    date: Instant,
    localDate: LocalDateTime,
    selectedDate: Instant,
    cellSize: Dp,
    selectedColor: Color,
    onDateClicked: (Int) -> Unit
) {
    val firstDayOfMonth by derivedStateOf {
        val shiftedDay = date.minus((localDate.dayOfMonth - 1).days)
        val firstWeekDay = shiftedDay.toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek
        firstWeekDay.isoDayNumber
    }

    val daysInAMonth by derivedStateOf { localDate.date.daysInAMonth() }
    val isContainsSelectedDate by derivedStateOf {
        selectedDate.monthsUntil(
            date,
            TimeZone.currentSystemDefault()
        ) == 0
    }
    val localSelectedDate by derivedStateOf { selectedDate.toLocalDateTime(TimeZone.currentSystemDefault()) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.Center,
        userScrollEnabled = false
    ) {
        for (i in 1 until (daysInAMonth + firstDayOfMonth)) {
            val dateValue = if (i < firstDayOfMonth) "" else "${i - firstDayOfMonth + 1}"
            val isSelectedDate =
                dateValue.isNotBlank() && isContainsSelectedDate && localSelectedDate.dayOfMonth == dateValue.toInt()

            item {
                var modifier = Modifier.size(cellSize)
                if (isSelectedDate) {
                    modifier = modifier.then(
                        Modifier.background(
                            selectedColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(cellSize)
                        )
                    )
                }

                Box(modifier = modifier) {
                    Text(
                        modifier = Modifier.align(Alignment.Center).clickable {
                            if (dateValue.isNotBlank()) {
                                onDateClicked.invoke(dateValue.toInt())
                            }
                        },
                        fontSize = 16.sp,
                        text = dateValue,
                        color = if (isSelectedDate) selectedColor else textColor
                    )
                }
            }
        }
    }
}
