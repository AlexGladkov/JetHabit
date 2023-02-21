package screens.daily.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.themes.JetHabitTheme

data class HabitCardItemModel(
    val habitId: Long,
    val title: String,
    val isChecked: Boolean
)

@Composable
internal fun HabitCardItem(
    model: HabitCardItemModel,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    onCardClicked: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .clickable { onCardClicked?.invoke() }
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
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
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