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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.alexgladkov.jetpackcomposedemo.R
import ru.alexgladkov.jetpackcomposedemo.screens.daily.views.HabbitCardItem
import ru.alexgladkov.jetpackcomposedemo.screens.daily.views.HabbitCardItemModel
import ru.alexgladkov.jetpackcomposedemo.screens.settings.views.MenuItem
import ru.alexgladkov.jetpackcomposedemo.screens.settings.views.MenuItemModel
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitCorners
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitSize
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitStyle
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitTheme
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
    currentTextSize: JetHabbitSize,
    currentPaddingSize: JetHabbitSize,
    currentCornersStyle: JetHabbitCorners,
    onDarkModeChanged: (Boolean) -> Unit,
    onNewStyle: (JetHabbitStyle) -> Unit,
    onTextSizeChanged: (JetHabbitSize) -> Unit,
    onPaddingSizeChanged: (JetHabbitSize) -> Unit,
    onCornersStyleChanged: (JetHabbitCorners) -> Unit,
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
                        .padding(start = JetHabbitTheme.shapes.padding),
                    text = stringResource(id = R.string.title_settings),
                    color = JetHabbitTheme.colors.primaryText,
                    style = JetHabbitTheme.typography.toolbar
                )
            }

            Row(
                modifier = Modifier.padding(JetHabbitTheme.shapes.padding)
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.action_dark_theme_enable),
                    color = JetHabbitTheme.colors.primaryText,
                    style = JetHabbitTheme.typography.body
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

            Divider(
                modifier = Modifier.padding(start = JetHabbitTheme.shapes.padding),
                thickness = 0.5.dp,
                color = JetHabbitTheme.colors.secondaryText.copy(
                    alpha = 0.3f
                )
            )

            MenuItem(
                model = MenuItemModel(
                    title = stringResource(id = R.string.title_font_size),
                    currentIndex = when (currentTextSize) {
                        JetHabbitSize.Small -> 0
                        JetHabbitSize.Medium -> 1
                        JetHabbitSize.Big -> 2
                    },
                    values = listOf(
                        stringResource(id = R.string.title_font_size_small),
                        stringResource(id = R.string.title_font_size_medium),
                        stringResource(id = R.string.title_font_size_big)
                    )
                ),
                onItemSelected = {
                    when (it) {
                        0 -> onTextSizeChanged.invoke(JetHabbitSize.Small)
                        1 -> onTextSizeChanged.invoke(JetHabbitSize.Medium)
                        2 -> onTextSizeChanged.invoke(JetHabbitSize.Big)
                    }
                }
            )

            MenuItem(
                model = MenuItemModel(
                    title = stringResource(id = R.string.title_padding_size),
                    currentIndex = when (currentPaddingSize) {
                        JetHabbitSize.Small -> 0
                        JetHabbitSize.Medium -> 1
                        JetHabbitSize.Big -> 2
                    },
                    values = listOf(
                        stringResource(id = R.string.title_padding_small),
                        stringResource(id = R.string.title_padding_medium),
                        stringResource(id = R.string.title_padding_big)
                    )
                ),
                onItemSelected = {
                    when (it) {
                        0 -> onPaddingSizeChanged.invoke(JetHabbitSize.Small)
                        1 -> onPaddingSizeChanged.invoke(JetHabbitSize.Medium)
                        2 -> onPaddingSizeChanged.invoke(JetHabbitSize.Big)
                    }
                }
            )

            MenuItem(
                model = MenuItemModel(
                    title = stringResource(id = R.string.title_corners_style),
                    currentIndex = when (currentCornersStyle) {
                        JetHabbitCorners.Rounded -> 0
                        JetHabbitCorners.Flat -> 1
                    },
                    values = listOf(
                        stringResource(id = R.string.title_corners_style_rounded),
                        stringResource(id = R.string.title_corners_style_flat)
                    )
                ),
                onItemSelected = {
                    when (it) {
                        0 -> onCornersStyleChanged.invoke(JetHabbitCorners.Rounded)
                        1 -> onCornersStyleChanged.invoke(JetHabbitCorners.Flat)
                    }
                }
            )

            Icon(painterResource(
                id = JetHabbitTheme.images.mainIcon),
                contentDescription = JetHabbitTheme.images.mainIconDescription
            )

            Row(
                modifier = Modifier
                    .padding(JetHabbitTheme.shapes.padding)
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
                    .padding(JetHabbitTheme.shapes.padding)
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
        shape = JetHabbitTheme.shapes.cornersStyle
    ) { }
}