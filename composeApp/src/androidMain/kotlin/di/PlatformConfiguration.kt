package di

import android.content.Context

actual class PlatformConfiguration constructor(val activityContext: Context, actual val appName: String) {
    actual val platform: Platform
        get() = Platform.Android
}