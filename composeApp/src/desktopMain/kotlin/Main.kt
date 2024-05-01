import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication

fun main() =
    singleWindowApplication(
        title = "Jet Habit",
        state = WindowState(size = DpSize(800.dp, 800.dp))
    ) {
        MainView()
    }
