package tech.mobiledeveloper.jethabit

import App
import MainView
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import core.database.getDatabaseBuilder
import di.LocalPlatform
import di.Platform
import di.PlatformConfiguration
import di.PlatformSDK
import tech.mobiledeveloper.jethabit.app.AppRes

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appDatabase = getDatabaseBuilder(applicationContext).build()

        PlatformSDK.init(
            configuration = PlatformConfiguration(
                activityContext = applicationContext,
                appName = AppRes.string.app_name
            ),
            appDatabase = appDatabase
        )

        setContent {
            CompositionLocalProvider(
                LocalPlatform provides Platform.Android
            ) {
                App()
            }
        }
    }
}