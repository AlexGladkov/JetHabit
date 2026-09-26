package desktop

import DesktopRuntime
import data.features.settings.SettingsEventBus
import di.Platform
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class DesktopRuntimeTest {
    @Test
    fun singleJvmLifetimeResource() {
        val database = Any()
        val eventBus = SettingsEventBus()
        var bootstrapCount = 0
        val runtime = DesktopRuntime(database, eventBus) { bootstrapCount++ }
        runtime.bootstrap()
        runtime.bootstrap()
        assertEquals(1, runtime.bootstrapCount)
        assertEquals(1, bootstrapCount)
        assertSame(database, runtime.appDatabase)
        assertSame(eventBus, runtime.settingsEventBus)
        assertEquals(Platform.Desktop, runtime.platform)
    }

    @Test
    fun cleanExit() {
        val runtime = DesktopRuntime(Any(), bootstrapAction = {})
        runtime.bootstrap()
        runtime.close()
        runtime.close()
        assertEquals(1, runtime.disposeCount)
    }
}
