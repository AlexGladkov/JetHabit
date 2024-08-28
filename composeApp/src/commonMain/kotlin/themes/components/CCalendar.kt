package themes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.stringResource
import tech.mobiledeveloper.jethabit.resources.*
import ui.themes.JetHabitTheme
import ui.themes.components.JetHabitButton
import utils.daysInMonth
import utils.wrap

@Composable
internal fun CCalendar(
    selectedDate: LocalDate,
    textColor: Color,
    dayOfWeekColor: Color,
    selectedColor: Color,
    cellSize: Dp = 40.dp,
    allowSameDate: Boolean = false,
    onDateSelected: (LocalDate) -> Unit,
) {
    var dateState by remember { mutableStateOf(selectedDate) }

    Surface(
        color = JetHabitTheme.colors.primaryBackground
    ) {
        Column {
            Row(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${dateState.month.name} ${dateState.year}",
                    color = textColor,
                    style = JetHabitTheme.typography.body,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier.clickable {
                        dateState = dateState.minus(1, DateTimeUnit.MONTH)
                    },
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    tint = JetHabitTheme.colors.tintColor,
                    contentDescription = "Month Back"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    modifier = Modifier.clickable {
                        dateState = dateState.plus(1, DateTimeUnit.MONTH)
                    },
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
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
                    text = stringResource(Res.string.days_monday_short),
                    color = dayOfWeekColor,
                    style = JetHabitTheme.typography.caption
                )
                Text(
                    modifier = Modifier.weight(1f).height(cellSize),
                    textAlign = TextAlign.Center,
                    text = stringResource(Res.string.days_tuesday_short),
                    color = dayOfWeekColor,
                    style = JetHabitTheme.typography.caption
                )
                Text(
                    modifier = Modifier.weight(1f).height(cellSize),
                    textAlign = TextAlign.Center,
                    text = stringResource(Res.string.days_wednesday_short),
                    color = dayOfWeekColor,
                    style = JetHabitTheme.typography.caption
                )
                Text(
                    modifier = Modifier.weight(1f).height(cellSize),
                    textAlign = TextAlign.Center,
                    text = stringResource(Res.string.days_thursday_short),
                    color = dayOfWeekColor,
                    style = JetHabitTheme.typography.caption
                )
                Text(
                    modifier = Modifier.weight(1f).height(cellSize),
                    textAlign = TextAlign.Center,
                    text = stringResource(Res.string.days_friday_short),
                    color = dayOfWeekColor,
                    style = JetHabitTheme.typography.caption
                )
                Text(
                    modifier = Modifier.weight(1f).height(cellSize),
                    textAlign = TextAlign.Center,
                    text = stringResource(Res.string.days_saturday_short),
                    color = dayOfWeekColor,
                    style = JetHabitTheme.typography.caption
                )
                Text(
                    modifier = Modifier.weight(1f).height(cellSize),
                    textAlign = TextAlign.Center,
                    text = stringResource(Res.string.days_sunday_short),
                    color = dayOfWeekColor,
                    style = JetHabitTheme.typography.caption
                )
            }

            DatesForMonth(textColor, dateState, cellSize, selectedColor) {
                dateState =
                    LocalDate.parse("${dateState.year}-${dateState.monthNumber.wrap()}-${it.wrap()}")
            }

            val isSame by derivedStateOf {
                selectedDate.compareTo(dateState) == 0
            }

            JetHabitButton(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                text = stringResource(Res.string.action_save),
                enabled = if (allowSameDate) true else !isSame,
                onClick = {
                    onDateSelected.invoke(dateState)
                })
        }
    }
}

@Composable
internal fun DatesForMonth(
    textColor: Color,
    date: LocalDate,
    cellSize: Dp,
    selectedColor: Color,
    onDateClicked: (Int) -> Unit
) {

    val firstDayOfMonth by derivedStateOf {
        val dayOfWeek = date.minus((date.dayOfMonth - 1), DateTimeUnit.DAY)
        dayOfWeek.dayOfWeek.ordinal
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.Center,
        userScrollEnabled = false
    ) {
        for (i in 1 until (date.daysInMonth() + firstDayOfMonth)) {
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
