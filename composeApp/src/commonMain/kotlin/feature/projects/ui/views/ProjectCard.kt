package feature.projects.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import feature.projects.data.ProjectEntity

@Composable
fun ProjectCard(
    project: ProjectEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = parseColor(project.colorHex),
                        shape = CircleShape
                    )
            )

            Text(
                text = project.title,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun parseColor(hexString: String): Color {
    return try {
        val cleanHex = hexString.removePrefix("#")
        val colorInt = cleanHex.toLong(16)
        Color(colorInt or 0xFF000000)
    } catch (e: Exception) {
        Color.Gray
    }
}
