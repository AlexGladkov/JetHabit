package feature.statistics.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.title_statistics
import ui.themes.JetHabitTheme

@Composable
internal fun StatisticsViewNoItems() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(Res.string.title_statistics),
            style = JetHabitTheme.typography.heading,
            color = JetHabitTheme.colors.primaryText
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Track your habits to see statistics",
            style = JetHabitTheme.typography.body,
            color = JetHabitTheme.colors.secondaryText,
            textAlign = TextAlign.Center
        )
    }
} 