package di

actual class PlatformConfiguration {
    actual val appName = "JetHabit"

    val appDataPath = when {
        System.getProperty("os.name").contains("Mac", true) -> "/Users/${System.getProperty("user.name")}/Library/Application Support/$appName"
        System.getProperty("os.name").contains("windows", true) -> "${System.getProperty("user.home")}\\AppData\\Local\\$appName"
        else -> throw IllegalStateException("This type OS not implemented")
    }
    actual val platform: Platform
        get() = Platform.Desktop
}