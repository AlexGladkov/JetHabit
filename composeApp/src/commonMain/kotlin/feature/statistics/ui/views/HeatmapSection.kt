package feature.statistics.ui.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import feature.daily.domain.DayCellStatus
import feature.daily.domain.HeatmapModel
import navigation.AppTestTags
import ui.themes.JetHabitTheme

private const val HEATMAP_ROWS = 7
private const val HEATMAP_COLUMNS = 12

/**
 * E7: weekly habit heatmap, the first section of the Statistics list.
 * 7 rows (Mon..Sun) x 12 weeks drawn on a Compose Canvas.
 */
@Composable
internal fun HeatmapSection(
    heatmap: HeatmapModel?,
    onDayCellClicked: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(AppTestTags.HeatmapSection),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Тепловая карта привычек",
            style = JetHabitTheme.typography.body,
            color = JetHabitTheme.colors.primaryText
        )

        if (heatmap == null || heatmap.dayDetails.isEmpty()) {
            HeatmapEmptyState()
        } else {
            HeatmapGrid(heatmap = heatmap, onDayCellClicked = onDayCellClicked)
            HeatmapLegend()
        }
    }
}

@Composable
private fun HeatmapEmptyState() {
    Text(
        text = "Статистики пока нет",
        style = JetHabitTheme.typography.body,
        color = JetHabitTheme.colors.secondaryText,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .testTag(AppTestTags.HeatmapEmptyState)
    )
}

@Composable
private fun HeatmapGrid(
    heatmap: HeatmapModel,
    onDayCellClicked: (Int) -> Unit
) {
    val doneColor = JetHabitTheme.colors.tintColor
    val noneColor = JetHabitTheme.colors.errorColor
    val emptyColor = JetHabitTheme.colors.secondaryBackground
    val partialColor = doneColor.copy(alpha = 0.45f)

    fun cellColor(status: DayCellStatus) = when (status) {
        DayCellStatus.DONE -> doneColor
        DayCellStatus.PARTIAL -> partialColor
        DayCellStatus.NONE -> noneColor.copy(alpha = 0.25f)
        DayCellStatus.EMPTY -> emptyColor
    }

    // Row-major overlay of clickable cells with stable a11y tags on top of the Canvas grid.
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        val gap = 2.dp
        val cellWidth = (maxWidth - gap * (HEATMAP_COLUMNS - 1)) / HEATMAP_COLUMNS
        val cellHeight = (maxHeight - gap * (HEATMAP_ROWS - 1)) / HEATMAP_ROWS

        Canvas(modifier = Modifier.fillMaxSize()) {
            val gapPx = gap.toPx()
            val cellW = cellWidth.toPx()
            val cellH = cellHeight.toPx()
            repeat(HEATMAP_ROWS) { row ->
                repeat(HEATMAP_COLUMNS) { column ->
                    val status = heatmap.grid.getOrNull(row)?.getOrNull(column) ?: DayCellStatus.EMPTY
                    drawRoundRect(
                        color = cellColor(status),
                        topLeft = Offset(
                            x = column * (cellW + gapPx),
                            y = row * (cellH + gapPx)
                        ),
                        size = Size(cellW, cellH),
                        cornerRadius = CornerRadius(4.dp.toPx() / 2)
                    )
                }
            }
        }

        repeat(HEATMAP_ROWS) { row ->
            repeat(HEATMAP_COLUMNS) { column ->
                val index = row * HEATMAP_COLUMNS + column
                val status = heatmap.grid.getOrNull(row)?.getOrNull(column) ?: DayCellStatus.EMPTY
                Box(
                    modifier = Modifier
                        .offset(x = (cellWidth + gap) * column, y = (cellHeight + gap) * row)
                        .size(cellWidth, cellHeight)
                        .testTag(AppTestTags.heatmapCell(index))
                        .semantics { contentDescription = "heatmap cell $index ${status.name}" }
                        .clickable { onDayCellClicked(index) }
                )
            }
        }
    }
}

@Composable
private fun HeatmapLegend() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(AppTestTags.HeatmapLegend),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeatmapLegendItem(color = JetHabitTheme.colors.tintColor, label = "Готово")
        HeatmapLegendItem(color = JetHabitTheme.colors.tintColor.copy(alpha = 0.45f), label = "Частично")
        HeatmapLegendItem(color = JetHabitTheme.colors.errorColor.copy(alpha = 0.25f), label = "Не выполнено")
        HeatmapLegendItem(color = JetHabitTheme.colors.secondaryBackground, label = "Нет данных")
    }
}

@Composable
private fun HeatmapLegendItem(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = color, shape = RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = JetHabitTheme.colors.secondaryText
        )
    }
}
