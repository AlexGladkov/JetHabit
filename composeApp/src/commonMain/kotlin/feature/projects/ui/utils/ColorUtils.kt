package feature.projects.ui.utils

import androidx.compose.ui.graphics.Color

/**
 * Parses a hex color string (e.g., "#FF5722" or "FF5722") into a Compose Color.
 * Returns Color.Gray if parsing fails.
 */
fun parseColor(hexString: String): Color {
    return try {
        val cleanHex = hexString.removePrefix("#")
        val colorInt = cleanHex.toLong(16)
        Color(colorInt or 0xFF000000)
    } catch (e: Exception) {
        Color.Gray
    }
}
