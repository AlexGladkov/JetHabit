package ru.alexgladkov.jetpackcomposedemo.screens.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.alexgladkov.jetpackcomposedemo.screens.daily.views.HabbitCardItem
import ru.alexgladkov.jetpackcomposedemo.screens.daily.views.HabbitCardItemModel
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitStyle
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.LocalJetHabbitColors
import ru.alexgladkov.jetpackcomposedemo.ui.themes.blueDarkPalette
import ru.alexgladkov.jetpackcomposedemo.ui.themes.blueLightPalette
import ru.alexgladkov.jetpackcomposedemo.ui.themes.orangeDarkPalette
import ru.alexgladkov.jetpackcomposedemo.ui.themes.orangeLightPalette
import ru.alexgladkov.jetpackcomposedemo.ui.themes.purpleDarkPalette
import ru.alexgladkov.jetpackcomposedemo.ui.themes.purpleLightPalette

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun SettingsScreen(
    onNewStyle: (JetHabbitStyle) -> Unit,
    onDarkModeChanged: (Boolean) -> Unit
) {
    val isDarkModeValue = isSystemInDarkTheme()

    val isDarkMode = remember {
        mutableStateOf(isDarkModeValue)
    }

    LazyColumn(
        Modifier.background(JetHabbitTheme.colors.primaryBackground).fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text(
                    modifier = Modifier.weight(1f), text = "Включить темную тему",
                    color = JetHabbitTheme.colors.primaryText
                )
                Checkbox(
                    checked = isDarkMode.value, onCheckedChange = {
                        isDarkMode.value = !isDarkMode.value
                        onDarkModeChanged.invoke(isDarkMode.value)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = JetHabbitTheme.colors.tintColor,
                        uncheckedColor = JetHabbitTheme.colors.secondaryText
                    )
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ColorCard(color = if (isSystemInDarkTheme()) purpleDarkPalette.tintColor else purpleLightPalette.tintColor,
                    onClick = {
                        onNewStyle.invoke(JetHabbitStyle.Purple)
                    })
                ColorCard(color = if (isSystemInDarkTheme()) orangeDarkPalette.tintColor else orangeLightPalette.tintColor,
                    onClick = {
                        onNewStyle.invoke(JetHabbitStyle.Orange)
                    })
                ColorCard(color = if (isSystemInDarkTheme()) blueDarkPalette.tintColor else blueLightPalette.tintColor,
                    onClick = {
                        onNewStyle.invoke(JetHabbitStyle.Blue)
                    })
            }
        }
    }
}

@Composable
fun ColorCard(
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(60.dp)
            .clickable {
                onClick.invoke()
            },
        backgroundColor = color,
        elevation = 8.dp,
        shape = RoundedCornerShape(4.dp)
    ) { }
}