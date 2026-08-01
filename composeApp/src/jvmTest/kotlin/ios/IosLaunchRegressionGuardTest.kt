package ios

import java.io.File
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class IosLaunchRegressionGuardTest {
    @Test
    fun iosMainKeepsLaunchInitializationInvariants(): Unit {
        val iosMain = readProjectFile(IOS_MAIN_PATHS)

        val databaseInitialization = "val appDatabase = getRoomDatabase(getDatabaseBuilder())"
        val platformSdkInit = "PlatformSDK.init(PlatformConfiguration(), appDatabase = appDatabase)"
        val composeEntryPoint = "return ComposeUIViewController {"

        val databaseInitializationIndex = assertContainsExactlyOnce(iosMain, databaseInitialization)
        val platformSdkInitIndex = assertContainsExactlyOnce(iosMain, platformSdkInit)
        val composeEntryPointIndex = assertContainsExactlyOnce(iosMain, composeEntryPoint)

        assertTrue(
            actual = databaseInitializationIndex < platformSdkInitIndex,
            message = "iOS database must be created before PlatformSDK.init receives it"
        )
        assertTrue(
            actual = platformSdkInitIndex < composeEntryPointIndex,
            message = "PlatformSDK.init must stay outside ComposeUIViewController recomposition path"
        )
        assertTrue(actual = iosMain.contains("CompositionLocalProvider(LocalPlatform provides Platform.iOS)"))
        assertTrue(actual = iosMain.contains("initializeCoil(PlatformContext.INSTANCE)"))
    }

    private fun assertContainsExactlyOnce(source: String, needle: String): Int {
        val matches = Regex.fromLiteral(needle).findAll(source).toList()

        assertTrue(
            actual = matches.singleOrNull() != null,
            message = "Expected iOS entrypoint to contain exactly one occurrence of: $needle"
        )
        return matches.single().range.first
    }

    private fun readProjectFile(paths: List<String>): String {
        val file = paths.asSequence()
            .map(::File)
            .firstOrNull { candidate -> candidate.isFile }

        assertNotNull(file, "Expected one of the project files to exist: ${paths.joinToString()}")
        return file.readText()
    }

    private companion object {
        private val IOS_MAIN_PATHS = listOf(
            "composeApp/src/iosMain/kotlin/main.ios.kt",
            "src/iosMain/kotlin/main.ios.kt"
        )
    }
}
