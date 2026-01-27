package feature.projects.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import feature.projects.data.ProjectEntity
import feature.projects.ui.utils.parseColor

private val PRESET_COLORS = listOf(
    "#FF5722", // Red
    "#4CAF50", // Green
    "#2196F3", // Blue
    "#FFC107", // Amber
    "#9C27B0", // Purple
    "#FF9800", // Orange
    "#00BCD4", // Cyan
    "#795548"  // Brown
)

@Composable
fun ProjectEditDialog(
    project: ProjectEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    onDelete: (() -> Unit)?
) {
    var title by remember(project) { mutableStateOf(project?.title ?: "") }
    var selectedColor by remember(project) { mutableStateOf(project?.colorHex ?: PRESET_COLORS[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (project == null) "Create Project" else "Edit Project")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Project Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Color", style = MaterialTheme.typography.subtitle2)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PRESET_COLORS.forEach { colorHex ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = parseColor(colorHex),
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorHex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, selectedColor)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                if (project != null && onDelete != null) {
                    TextButton(onClick = onDelete) {
                        Text("Delete", color = MaterialTheme.colors.error)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
