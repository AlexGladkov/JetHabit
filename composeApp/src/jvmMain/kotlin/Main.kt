import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import core.database.getDatabaseBuilder
import core.database.getRoomDatabase

fun main() =
    singleWindowApplication(
        title = "Jet Habit",
        state = WindowState(size = DpSize(800.dp, 800.dp))
    ) {
        val runtime = remember {
            DesktopRuntime(getRoomDatabase(getDatabaseBuilder())).also { it.bootstrap() }
        }
        DisposableEffect(runtime) {
            onDispose { runtime.close() }
        }
        MainView(runtime)
    }
