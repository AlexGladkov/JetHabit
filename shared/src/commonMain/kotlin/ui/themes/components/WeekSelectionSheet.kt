package ui.themes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.mobiledeveloper.shared.AppRes
import ui.themes.JetHabitTheme
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign

@Composable
internal fun WeekSelectionSheet(
    currentState: List<Boolean>,
    onSaveClicked: (List<Boolean>) -> Unit,
    onCloseClick: () -> Unit
) {
    var selectedDays by remember { mutableStateOf(currentState) }
    var isErrorDisplay by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.background(JetHabitTheme.colors.primaryBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = AppRes.string.medication_periodicity,
                color = JetHabitTheme.colors.primaryText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                modifier = Modifier.clickable {
                    onCloseClick.invoke()
                }.size(48.dp).padding(10.dp),
                imageVector = Icons.Filled.Close,
                tint = JetHabitTheme.colors.controlColor,
                contentDescription = "Close"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().height(100.dp)
                .background(JetHabitTheme.colors.secondaryBackground),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in 0 until 7) {
                DayOfWeekCard(
                    title = when (i) {
                        0 -> AppRes.string.days_monday_short
                        1 -> AppRes.string.days_tuesday_short
                        2 -> AppRes.string.days_wednesday_short
                        3 -> AppRes.string.days_thursday_short
                        4 -> AppRes.string.days_friday_short
                        5 -> AppRes.string.days_saturday_short
                        6 -> AppRes.string.days_sunday_short
                        else -> throw IllegalStateException()
                    },
                    isSelected = selectedDays[i],
                    onDateClicked = {
                        val mutableList = selectedDays.toMutableList()
                        mutableList[i] = !it
                        selectedDays = mutableList
                    }
                )

                if (i < 6)
                    Spacer(modifier = Modifier.width(8.dp))
            }
        }

        JetHabitButton(modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .fillMaxWidth(),
            text = AppRes.string.action_save,
            onClick = {
                val containsSelected = selectedDays.count { it } > 0
                if (containsSelected)
                    onSaveClicked.invoke(selectedDays)
                else
                    isErrorDisplay = true
            }
        )

        if (isErrorDisplay) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                text = AppRes.string.medication_periodicity_error,
                color = JetHabitTheme.colors.errorColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
internal fun DayOfWeekCard(title: String, isSelected: Boolean, onDateClicked: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .clickable { onDateClicked.invoke(isSelected) }
            .size(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) JetHabitTheme.colors.tintColor else JetHabitTheme.colors.primaryBackground
            )
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = title,
            color = JetHabitTheme.colors.primaryText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}