package ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import feature.projects.data.ProjectEntity
import org.jetbrains.compose.resources.stringResource
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.project_filter_all
import tech.mobiledeveloper.jethabit.resources.project_filter_uncategorized
import ui.themes.JetHabitTheme

@Composable
internal fun ProjectChipBar(
    projects: List<ProjectEntity>,
    selectedProjectId: String?,
    isUncategorizedSelected: Boolean,
    onAllSelected: () -> Unit,
    onUncategorizedSelected: () -> Unit,
    onProjectSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(bottom = JetHabitTheme.shapes.padding),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProjectFilterChip(
            title = stringResource(Res.string.project_filter_all),
            isSelected = selectedProjectId == null && !isUncategorizedSelected,
            onClick = onAllSelected
        )

        ProjectFilterChip(
            title = stringResource(Res.string.project_filter_uncategorized),
            isSelected = isUncategorizedSelected,
            onClick = onUncategorizedSelected
        )

        projects.forEach { project ->
            ProjectFilterChip(
                title = project.title,
                isSelected = selectedProjectId == project.id && !isUncategorizedSelected,
                onClick = { onProjectSelected(project.id) }
            )
        }
    }
}

@Composable
private fun ProjectFilterChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = if (isSelected) JetHabitTheme.colors.tintColor else Color.Transparent
        )
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else JetHabitTheme.colors.primaryText,
            style = JetHabitTheme.typography.body
        )
    }
}
