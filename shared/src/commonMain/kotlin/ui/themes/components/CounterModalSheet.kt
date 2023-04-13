package ui.themes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.mobiledeveloper.shared.AppRes
import ui.themes.JetHabitTheme

@Composable
internal fun CounterModalSheet(
    title: String, count: String,
    onCountClicked: (String) -> Unit,
    onCloseClick: () -> Unit
) {
    var countValue by remember { mutableStateOf(count) }

    Column(
        modifier = Modifier
            .background(JetHabitTheme.colors.primaryBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = title,
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

        Divider(
            modifier = Modifier.fillMaxWidth().height(0.5.dp),
            color = JetHabitTheme.colors.controlColor.copy(0.3f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = countValue,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            color = JetHabitTheme.colors.primaryText
        )

        JetHabitButton(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                .fillMaxWidth(),
            text = AppRes.string.action_save,
            onClick = {
                onCountClicked.invoke(countValue)
            })

        Column(
            modifier = Modifier.padding(top = 20.dp).fillMaxWidth()
                .background(JetHabitTheme.colors.secondaryBackground)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Row {
                CardButton("1") { countValue = setupValue(countValue, it) }
                CardButton("2") { countValue = setupValue(countValue, it) }
                CardButton("3") { countValue = setupValue(countValue, it) }
            }

            Row {
                CardButton("4") { countValue = setupValue(countValue, it) }
                CardButton("5") { countValue = setupValue(countValue, it) }
                CardButton("6") { countValue = setupValue(countValue, it) }
            }

            Row {
                CardButton("7") { countValue = setupValue(countValue, it) }
                CardButton("8") { countValue = setupValue(countValue, it) }
                CardButton("9") { countValue = setupValue(countValue, it) }
            }

            Row {
                Box(modifier = Modifier.weight(1f))
                CardButton("0") { countValue = setupValue(countValue, it) }
                CardButton("<") {
                    countValue = if (countValue.count() == 1) {
                        "0"
                    } else {
                        countValue.dropLast(1)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

private fun setupValue(currentValue: String, new: String): String {
    return if (currentValue == "0") {
        new
    } else {
        "$currentValue$new"
    }
}

@Composable
internal fun RowScope.CardButton(value: String, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier.padding(4.dp)
            .clickable { onClick.invoke(value) }.weight(1f).height(48.dp),
        backgroundColor = JetHabitTheme.colors.primaryBackground,
        elevation = 0.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = value,
                color = JetHabitTheme.colors.primaryText,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
            )
        }
    }
}