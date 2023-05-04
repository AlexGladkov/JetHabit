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
internal fun TimesOfDaySelectionSheet(
    currentState: List<Boolean>,
    onSaveClicked: (List<Boolean>) -> Unit,
    onCloseClick: () -> Unit
) {
    var selectedTimes by remember { mutableStateOf(currentState) }
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
                text = AppRes.string.medication_frequency,
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
            for (i in 0 until 3) {
                TimesOfDayCard(
                    title = when (i) {
                        0 -> AppRes.string.times_of_day_morning
                        1 -> AppRes.string.times_of_day_afternoon
                        2 -> AppRes.string.times_of_day_evening
                        else -> throw IllegalStateException()
                    },
                    isSelected = selectedTimes[i],
                    onDateClicked = {
                        val mutableList = selectedTimes.toMutableList()
                        mutableList[i] = !it
                        selectedTimes = mutableList
                    }
                )

                if (i < 2)
                    Spacer(modifier = Modifier.width(8.dp))
            }
        }

        JetHabitButton(modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .fillMaxWidth(),
            text = AppRes.string.action_save,
            onClick = {
                val containsSelected = selectedTimes.count { it } > 0
                if (containsSelected)
                    onSaveClicked.invoke(selectedTimes)
                else
                    isErrorDisplay = true
            }
        )

        if (isErrorDisplay) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                text = AppRes.string.medication_frequency_error,
                color = JetHabitTheme.colors.errorColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
internal fun TimesOfDayCard(title: String, isSelected: Boolean, onDateClicked: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .clickable { onDateClicked.invoke(isSelected) }
            .size(height = 40.dp, width = 80.dp)
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