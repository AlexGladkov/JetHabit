package ru.alexgladkov.jetpackcomposedemo.screens.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.alexgladkov.jetpackcomposedemo.R
import ru.alexgladkov.jetpackcomposedemo.screens.daily.views.HabbitCardItem
import ru.alexgladkov.jetpackcomposedemo.screens.daily.views.HabbitCardItemModel
import ru.alexgladkov.jetpackcomposedemo.screens.settings.views.MenuItem
import ru.alexgladkov.jetpackcomposedemo.screens.settings.views.MenuItemModel
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitCorners
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitSize
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitStyle
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitTheme
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
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    currentTextSize: JetHabitSize,
    currentPaddingSize: JetHabitSize,
    currentCornersStyle: JetHabitCorners,
    onDarkModeChanged: (Boolean) -> Unit,
    onNewStyle: (JetHabitStyle) -> Unit,
    onTextSizeChanged: (JetHabitSize) -> Unit,
    onPaddingSizeChanged: (JetHabitSize) -> Unit,
    onCornersStyleChanged: (JetHabitCorners) -> Unit,
) {
    Surface(
        modifier = modifier,
        color = JetHabitTheme.colors.primaryBackground,
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {
            TopAppBar(
                backgroundColor = JetHabitTheme.colors.primaryBackground,
                elevation = 8.dp
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = JetHabitTheme.shapes.padding),
                    text = stringResource(id = R.string.title_settings),
                    color = JetHabitTheme.colors.primaryText,
                    style = JetHabitTheme.typography.toolbar
                )
            }

            Row(
                modifier = Modifier.padding(JetHabitTheme.shapes.padding)
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.action_dark_theme_enable),
                    color = JetHabitTheme.colors.primaryText,
                    style = JetHabitTheme.typography.body
                )
                Checkbox(
                    checked = isDarkMode, onCheckedChange = {
                        onDarkModeChanged.invoke(it)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = JetHabitTheme.colors.tintColor,
                        uncheckedColor = JetHabitTheme.colors.secondaryText
                    )
                )
            }

            Divider(
                modifier = Modifier.padding(start = JetHabitTheme.shapes.padding),
                thickness = 0.5.dp,
                color = JetHabitTheme.colors.secondaryText.copy(
                    alpha = 0.3f
                )
            )

            MenuItem(
                model = MenuItemModel(
                    title = stringResource(id = R.string.title_font_size),
                    currentIndex = when (currentTextSize) {
                        JetHabitSize.Small -> 0
                        JetHabitSize.Medium -> 1
                        JetHabitSize.Big -> 2
                    },
                    values = listOf(
                        stringResource(id = R.string.title_font_size_small),
                        stringResource(id = R.string.title_font_size_medium),
                        stringResource(id = R.string.title_font_size_big)
                    )
                ),
                onItemSelected = {
                    when (it) {
                        0 -> onTextSizeChanged.invoke(JetHabitSize.Small)
                        1 -> onTextSizeChanged.invoke(JetHabitSize.Medium)
                        2 -> onTextSizeChanged.invoke(JetHabitSize.Big)
                    }
                }
            )

            MenuItem(
                model = MenuItemModel(
                    title = stringResource(id = R.string.title_padding_size),
                    currentIndex = when (currentPaddingSize) {
                        JetHabitSize.Small -> 0
                        JetHabitSize.Medium -> 1
                        JetHabitSize.Big -> 2
                    },
                    values = listOf(
                        stringResource(id = R.string.title_padding_small),
                        stringResource(id = R.string.title_padding_medium),
                        stringResource(id = R.string.title_padding_big)
                    )
                ),
                onItemSelected = {
                    when (it) {
                        0 -> onPaddingSizeChanged.invoke(JetHabitSize.Small)
                        1 -> onPaddingSizeChanged.invoke(JetHabitSize.Medium)
                        2 -> onPaddingSizeChanged.invoke(JetHabitSize.Big)
                    }
                }
            )

            MenuItem(
                model = MenuItemModel(
                    title = stringResource(id = R.string.title_corners_style),
                    currentIndex = when (currentCornersStyle) {
                        JetHabitCorners.Rounded -> 0
                        JetHabitCorners.Flat -> 1
                    },
                    values = listOf(
                        stringResource(id = R.string.title_corners_style_rounded),
                        stringResource(id = R.string.title_corners_style_flat)
                    )
                ),
                onItemSelected = {
                    when (it) {
                        0 -> onCornersStyleChanged.invoke(JetHabitCorners.Rounded)
                        1 -> onCornersStyleChanged.invoke(JetHabitCorners.Flat)
                    }
                }
            )

            Row(
                modifier = Modifier
                    .padding(JetHabitTheme.shapes.padding)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ColorCard(color = if (isDarkMode) purpleDarkPalette.tintColor else purpleLightPalette.tintColor,
                    onClick = {
                        onNewStyle.invoke(JetHabitStyle.Purple)
                    })
                ColorCard(color = if (isDarkMode) orangeDarkPalette.tintColor else orangeLightPalette.tintColor,
                    onClick = {
                        onNewStyle.invoke(JetHabitStyle.Orange)
                    })
                ColorCard(color = if (isDarkMode) blueDarkPalette.tintColor else blueLightPalette.tintColor,
                    onClick = {
                        onNewStyle.invoke(JetHabitStyle.Blue)
                    })
            }

            Row(
                modifier = Modifier
                    .padding(JetHabitTheme.shapes.padding)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ColorCard(color = if (isDarkMode) redDarkPalette.tintColor else redLightPalette.tintColor,
                    onClick = {
                        onNewStyle.invoke(JetHabitStyle.Red)
                    })
                ColorCard(color = if (isDarkMode) greenDarkPalette.tintColor else greenLightPalette.tintColor,
                    onClick = {
                        onNewStyle.invoke(JetHabitStyle.Green)
                    })
            }

            HabbitCardItem(
                model = HabbitCardItemModel(
                    habbitId = 0,
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
        shape = JetHabitTheme.shapes.cornersStyle
    ) { }
}