package ru.alexgladkov.jetpackcomposedemo.screens.daily.views

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxColors
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.MainTheme

data class HabbitCardItemModel(
    val title: String,
    val isChecked: Boolean
)

@Composable
fun HabbitCardItem(
    model: HabbitCardItemModel,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        elevation = 8.dp,
        backgroundColor = JetHabbitTheme.colors.primaryBackground,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = model.title,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = JetHabbitTheme.colors.primaryText
            )

            Checkbox(
                checked = model.isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = JetHabbitTheme.colors.tintColor,
                    uncheckedColor = JetHabbitTheme.colors.secondaryText
                )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HabbitPurpleLightItem_Preview() {
    MainTheme(darkTheme = false) {
        HabbitCardItem(
            HabbitCardItemModel(
                title = "Чистить зубы",
                isChecked = true
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
fun HabbitPurpleDarkItem_Preview() {
    MainTheme(darkTheme = true) {
        HabbitCardItem(
            HabbitCardItemModel(
                title = "Чистить зубы",
                isChecked = true
            )
        )
    }
}