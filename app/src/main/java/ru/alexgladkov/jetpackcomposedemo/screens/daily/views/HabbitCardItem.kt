package ru.alexgladkov.jetpackcomposedemo.screens.daily.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.alexgladkov.jetpackcomposedemo.data.features.habbit.HabitEntity
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitStyle
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.MainTheme

data class HabbitCardItemModel(
    val habbitId: Long,
    val title: String,
    val isChecked: Boolean
)

fun HabitEntity.mapToCardItem() =
    HabbitCardItemModel(
        habbitId = this.itemId,
        title = this.title,
        isChecked = false
    )

@Composable
fun HabbitCardItem(
    model: HabbitCardItemModel,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .padding(
                horizontal = JetHabitTheme.shapes.padding,
                vertical = JetHabitTheme.shapes.padding / 2
            )
            .fillMaxWidth(),
        elevation = 8.dp,
        backgroundColor = JetHabitTheme.colors.primaryBackground,
        shape = JetHabitTheme.shapes.cornersStyle
    ) {
        Row(
            modifier = Modifier
                .padding(JetHabitTheme.shapes.padding)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = model.title,
                style = JetHabitTheme.typography.body,
                color = JetHabitTheme.colors.primaryText
            )

            Checkbox(
                checked = model.isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = JetHabitTheme.colors.tintColor,
                    uncheckedColor = JetHabitTheme.colors.secondaryText
                )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HabbitPurpleLightItem_Preview() {
    ThemedHabbitCard(isDarkMode = false, style = JetHabitStyle.Purple)
}

@Composable
@Preview(showBackground = true)
fun HabbitPurpleDarkItem_Preview() {
    ThemedHabbitCard(isDarkMode = true, style = JetHabitStyle.Purple)
}

@Composable
@Preview(showBackground = true)
fun HabbitRedLightItem_Preview() {
    ThemedHabbitCard(isDarkMode = false, style = JetHabitStyle.Red)
}

@Composable
@Preview(showBackground = true)
fun HabbitRedDarkItem_Preview() {
    ThemedHabbitCard(isDarkMode = true, style = JetHabitStyle.Red)
}

@Composable
@Preview(showBackground = true)
fun HabbitGreenLightItem_Preview() {
    ThemedHabbitCard(isDarkMode = false, style = JetHabitStyle.Green)
}

@Composable
@Preview(showBackground = true)
fun HabbitGreenDarkItem_Preview() {
    ThemedHabbitCard(isDarkMode = true, style = JetHabitStyle.Green)
}

@Composable
@Preview(showBackground = true)
fun HabbitOrangeLightItem_Preview() {
    ThemedHabbitCard(isDarkMode = false, style = JetHabitStyle.Orange)
}

@Composable
@Preview(showBackground = true)
fun HabbitOrangeDarkItem_Preview() {
    ThemedHabbitCard(isDarkMode = true, style = JetHabitStyle.Orange)
}

@Composable
@Preview(showBackground = true)
fun HabbitBlueLightItem_Preview() {
    ThemedHabbitCard(isDarkMode = false, style = JetHabitStyle.Blue)
}

@Composable
@Preview(showBackground = true)
fun HabbitBlueDarkItem_Preview() {
    ThemedHabbitCard(isDarkMode = true, style = JetHabitStyle.Blue)
}

@Composable
private fun ThemedHabbitCard(
    isDarkMode: Boolean,
    style: JetHabitStyle
) {
    MainTheme(darkTheme = isDarkMode, style = style) {
        Surface(
            color = JetHabitTheme.colors.primaryBackground
        ) {
            HabbitCardItem(
                HabbitCardItemModel(
                    habbitId = 0,
                    title = "Чистить зубы",
                    isChecked = true
                )
            )
        }
    }
}