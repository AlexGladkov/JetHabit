package screens.daily.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import feature.daily.presentation.models.DailyHabit
import feature.habits.data.HabitType
import org.jetbrains.compose.resources.stringResource
import tech.mobiledeveloper.jethabit.resources.*
import ui.themes.JetHabitTheme

data class HabitCardItemModel(
    val habitId: String,
    val title: String,
    val isChecked: Boolean,
    val type: HabitType = HabitType.REGULAR,
    val value: Double? = null
)

fun DailyHabit.mapToHabitCardItemModel(): HabitCardItemModel =
    HabitCardItemModel(
        habitId = id,
        title = title,
        isChecked = isChecked,
        type = type,
        value = value
    )

@Composable
internal fun HabitCardItem(
    model: HabitCardItemModel,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    onValueChange: ((Double) -> Unit)? = null,
    onCardClicked: (() -> Unit)? = null
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
                .fillMaxWidth()
                .padding(JetHabitTheme.shapes.padding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onCardClicked?.invoke() },
                text = model.title,
                style = JetHabitTheme.typography.body,
                color = JetHabitTheme.colors.primaryText
            )

            when (model.type) {
                HabitType.REGULAR -> {
                    Box(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Checkbox(
                            checked = model.isChecked,
                            onCheckedChange = onCheckedChange,
                            colors = CheckboxDefaults.colors(
                                checkedColor = JetHabitTheme.colors.tintColor,
                                uncheckedColor = JetHabitTheme.colors.secondaryText,
                                checkmarkColor = JetHabitTheme.colors.primaryBackground
                            )
                        )
                    }
                }
                HabitType.TRACKER -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (model.value != null) {
                            Text(
                                text = stringResource(Res.string.tracker_value_kg, model.value.toString()),
                                style = JetHabitTheme.typography.body,
                                color = JetHabitTheme.colors.secondaryText,
                                textAlign = TextAlign.End
                            )
                        }
                        
                        Box(
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Checkbox(
                                checked = model.isChecked,
                                onCheckedChange = onCheckedChange,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = JetHabitTheme.colors.tintColor,
                                    uncheckedColor = JetHabitTheme.colors.secondaryText,
                                    checkmarkColor = JetHabitTheme.colors.primaryBackground
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

//@Composable
//@Preview(showBackground = true)
//fun HabitPurpleLightItem_Preview() {
//    ThemedHabitCard(isDarkMode = false, style = JetHabitStyle.Purple)
//}
//
//@Composable
//@Preview(showBackground = true)
//fun HabitPurpleDarkItem_Preview() {
//    ThemedHabitCard(isDarkMode = true, style = JetHabitStyle.Purple)
//}
//
//@Composable
//@Preview(showBackground = true)
//fun HabitRedLightItem_Preview() {
//    ThemedHabitCard(isDarkMode = false, style = JetHabitStyle.Red)
//}
//
//@Composable
//@Preview(showBackground = true)
//fun HabitRedDarkItem_Preview() {
//    ThemedHabitCard(isDarkMode = true, style = JetHabitStyle.Red)
//}
//
//@Composable
//@Preview(showBackground = true)
//fun HabitGreenLightItem_Preview() {
//    ThemedHabitCard(isDarkMode = false, style = JetHabitStyle.Green)
//}
//
//@Composable
//@Preview(showBackground = true)
//fun HabitGreenDarkItem_Preview() {
//    ThemedHabitCard(isDarkMode = true, style = JetHabitStyle.Green)
//}
//
//@Composable
//@Preview(showBackground = true)
//fun HabitOrangeLightItem_Preview() {
//    ThemedHabitCard(isDarkMode = false, style = JetHabitStyle.Orange)
//}
//
//@Composable
//@Preview(showBackground = true)
//fun HabitOrangeDarkItem_Preview() {
//    ThemedHabitCard(isDarkMode = true, style = JetHabitStyle.Orange)
//}
//
//@Composable
//@Preview(showBackground = true)
//fun HabitBlueLightItem_Preview() {
//    ThemedHabitCard(isDarkMode = false, style = JetHabitStyle.Blue)
//}
//
//@Composable
//@Preview(showBackground = true)
//fun HabitBlueDarkItem_Preview() {
//    ThemedHabitCard(isDarkMode = true, style = JetHabitStyle.Blue)
//}
//
//@Composable
//private fun ThemedHabitCard(
//    isDarkMode: Boolean,
//    style: JetHabitStyle
//) {
//    MainTheme(darkTheme = isDarkMode, style = style) {
//        Surface(
//            color = JetHabitTheme.colors.primaryBackground
//        ) {
//            HabitCardItem(
//                HabitCardItemModel(
//                    habitId = 0,
//                    title = "Чистить зубы",
//                    isChecked = true
//                )
//            )
//        }
//    }
//}