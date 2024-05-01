package di

actual class PlatformConfiguration() {
    actual val appName: String
        get() = "JetHabit"
    actual val platform: Platform
        get() = Platform.MacOS
}