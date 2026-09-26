import data.features.settings.SettingsEventBus
import di.Platform
import di.PlatformConfiguration
import di.PlatformSDK

/** Desktop composition-root lifetime, created once by the application scope. */
class DesktopRuntime(
    val appDatabase: Any,
    val settingsEventBus: SettingsEventBus = SettingsEventBus(),
    val platform: Platform = Platform.Desktop,
    private val bootstrapAction: () -> Unit = {
        PlatformSDK.init(PlatformConfiguration(), appDatabase = appDatabase)
    }
) {
    private var bootstrapped = false
    private var closed = false
    var bootstrapCount: Int = 0
        private set
    var disposeCount: Int = 0
        private set

    fun bootstrap() {
        if (bootstrapped) return
        bootstrapped = true
        bootstrapCount++
        bootstrapAction()
    }

    fun close() {
        if (closed) return
        closed = true
        bootstrapped = false
        disposeCount++
        (appDatabase as? AutoCloseable)?.close()
    }
}
