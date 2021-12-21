package ru.alexgladkov.jetpackcomposedemo.screens.splash

import android.window.SplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitTheme

@Composable
fun SplashScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JetHabitTheme.colors.primaryBackground)
    ) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Jet Habit",
                style = JetHabitTheme.typography.heading,
                color = JetHabitTheme.colors.primaryText,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                text = "Full Compose Demo",
                style = JetHabitTheme.typography.body,
                color = JetHabitTheme.colors.secondaryText,
                textAlign = TextAlign.Center
            )
        }
    }

    LaunchedEffect(key1 = Unit, block = {
        navController.navigate("main")
    })
}