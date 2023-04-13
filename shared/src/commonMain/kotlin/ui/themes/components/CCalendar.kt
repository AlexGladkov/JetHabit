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
import com.soywiz.klock.DateTime
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.TimezoneOffset
import com.soywiz.klock.days
import com.soywiz.klock.months
import tech.mobiledeveloper.shared.AppRes
import ui.themes.JetHabitTheme
import utils.wrap

@Composable
internal fun CCalendar(
    selectedDate: DateTime,
    textColor: Color,
    dayOfWeekColor: Color,
    selectedColor: Color,
    cellSize: Dp = 40.dp,
    allowSameDate: Boolean = false,
    onDateSelected: (DateTime) -> Unit,
) {
    var dateState by remember { mutableStateOf(selectedDate) }

    Column {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${dateState.month.localShortName} ${dateState.year.year}",
                color = textColor,
                style = JetHabitTheme.typography.body,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                modifier = Modifier.clickable {
                    dateState = dateState.minus(1.months)
                },
                imageVector = Icons.Filled.KeyboardArrowLeft,
                tint = JetHabitTheme.colors.tintColor,
                contentDescription = "Month Back"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier.clickable {
                    dateState = dateState.plus(1.months)
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

        DatesForMonth(textColor, dateState, cellSize, selectedColor) {
            dateState =
                DateTime.fromString("${dateState.yearInt}-${dateState.month1.wrap()}-${it.wrap()}").local
        }

        val isSame by derivedStateOf {
            selectedDate.yearInt == dateState.yearInt &&
                    selectedDate.month0 == dateState.month0 &&
                    selectedDate.dayOfMonth == dateState.dayOfMonth
        }

        JetHabitButton(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            text = AppRes.string.action_save,
            enabled = if (allowSameDate) true else !isSame,
            onClick = {
                onDateSelected.invoke(dateState)
            })
    }
}

@Composable
internal fun DatesForMonth(
    textColor: Color,
    date: DateTime,
    cellSize: Dp,
    selectedColor: Color,
    onDateClicked: (Int) -> Unit
) {

    val firstDayOfMonth by derivedStateOf {
        val dayOfWeek = date.minus((date.dayOfMonth - 1).days)
        dayOfWeek.dayOfWeekInt
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.Center,
        userScrollEnabled = false
    ) {
        for (i in 1 until (date.month.days(date.year.year) + firstDayOfMonth)) {
            val dateValue = if (i < firstDayOfMonth) "" else "${i - firstDayOfMonth + 1}"
            val isSelectedDate =
                dateValue.isNotBlank() && dateValue == date.dayOfMonth.toString()

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
