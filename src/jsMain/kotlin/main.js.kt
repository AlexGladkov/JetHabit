import androidx.compose.ui.window.Window
import navigation.navigationGraph
import org.jetbrains.skiko.wasm.onWasmReady
import ru.alexgladkov.odyssey.compose.setup.OdysseyConfiguration
import ru.alexgladkov.odyssey.compose.setup.setNavigationContent
import ui.themes.JetHabitTheme
import ui.themes.MainTheme

fun main() {
    onWasmReady {
        Window {
            MainTheme {
                val odysseyConfiguration = OdysseyConfiguration(
                    backgroundColor = JetHabitTheme.colors.primaryBackground
                )

                setNavigationContent(odysseyConfiguration) {
                    navigationGraph()
                }
            }
        }
    }
}