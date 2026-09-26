package desktop

import DesktopRuntime
import data.features.settings.SettingsEventBus
import di.Platform
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertNotNull

class DesktopRuntimeTest {
    @Test
    fun singleJvmLifetimeResource() {
        bootstrapIsExactlyOnceAndResourcesRemainStable()
    }

    @Test
    fun cleanExit() {
        var closeCount = 0
        val resource = AutoCloseable { closeCount++ }
        val runtime = DesktopRuntime(resource, bootstrapAction = {})
        runtime.bootstrap()
        runtime.close()
        runtime.close()
        assertEquals(1, runtime.bootstrapCount)
        assertEquals(1, runtime.disposeCount)
        assertEquals(1, closeCount)
    }

    private fun bootstrapIsExactlyOnceAndResourcesRemainStable() {
        val database = Any()
        val eventBus = SettingsEventBus()
        var platformBootstrapCount = 0
        val runtime = DesktopRuntime(database, eventBus) { platformBootstrapCount++ }

        runtime.bootstrap()
        runtime.bootstrap()

        assertEquals(1, runtime.bootstrapCount)
        assertEquals(1, platformBootstrapCount)
        assertSame(database, runtime.appDatabase)
        assertSame(eventBus, runtime.settingsEventBus)
        assertEquals(Platform.Desktop, runtime.platform)
    }
}
