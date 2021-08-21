package ru.alexgladkov.jetpackcomposedemo.screens.settings

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.alexgladkov.jetpackcomposedemo.screens.daily.views.HabbitCardItem
import ru.alexgladkov.jetpackcomposedemo.screens.daily.views.HabbitCardItemModel
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitStyle
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.LocalJetHabbitColors
import ru.alexgladkov.jetpackcomposedemo.ui.themes.blueDarkPalette
import ru.alexgladkov.jetpackcomposedemo.ui.themes.blueLightPalette
import ru.alexgladkov.jetpackcomposedemo.ui.themes.greenDarkPalette
import ru.alexgladkov.jetpackcomposedemo.ui.themes.greenLightPalette
import ru.alexgladkov.jetpackcomposedemo.ui.themes.orangeDarkPalette
import ru.alexgladkov.jetpackcomposedemo.ui.themes.orangeLightPalette
import ru.alexgladkov.jetpackcomposedemo.ui.themes.purpleDarkPalette
import ru.alexgladkov.jetpackcomposedemo.ui.themes.purpleLightPalette
import ru.alexgladkov.jetpackcomposedemo.ui.themes.redDarkPalette
import ru.alexgladkov.jetpackcomposedemo.ui.themes.redLightPalette

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onDarkModeChanged: (Boolean) -> Unit,
    onNewStyle: (JetHabbitStyle) -> Unit
) {
    Surface(
        color = JetHabbitTheme.colors.primaryBackground,
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {
            TopAppBar(
                backgroundColor = JetHabbitTheme.colors.primaryBackground,
                elevation = 8.dp
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    text = "Настройки",
                    color = JetHabbitTheme.colors.primaryText,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }

            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    modifier = Modifier.weight(1f), text = "Включить темную тему",
                    color = JetHabbitTheme.colors.primaryText
                )
                Checkbox(
                    checked = isDarkMode, onCheckedChange = {
                        onDarkModeChanged.invoke(it)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = JetHabbitTheme.colors.tintColor,
                        uncheckedColor = JetHabbitTheme.colors.secondaryText
                    )
                )
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ColorCard(color = if (isDarkMode) purpleDarkPalette.tintColor else purpleLightPalette.tintColor,
                    onClick = {
                        onNewStyle.invoke(JetHabbitStyle.Purple)
                    })
                ColorCard(color = if (isDarkMode) orangeDarkPalette.tintColor else orangeLightPalette.tintColor,
                    onClick = {
                        onNewStyle.invoke(JetHabbitStyle.Orange)
                    })
                ColorCard(color = if (isDarkMode) blueDarkPalette.tintColor else blueLightPalette.tintColor,
                    onClick = {
                        onNewStyle.invoke(JetHabbitStyle.Blue)
                    })
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ColorCard(color = if (isDarkMode) redDarkPalette.tintColor else redLightPalette.tintColor,
                    onClick = {
                        onNewStyle.invoke(JetHabbitStyle.Red)
                    })
                ColorCard(color = if (isDarkMode) greenDarkPalette.tintColor else greenLightPalette.tintColor,
                    onClick = {
                        onNewStyle.invoke(JetHabbitStyle.Green)
                    })
            }

            HabbitCardItem(
                model = HabbitCardItemModel(
                    title = "Пример карточки",
                    isChecked = true
                )
            )
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