package tech.mobiledeveloper.jethabit

import App
import MainView
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import di.LocalPlatform
import di.Platform
import di.PlatformConfiguration
import di.PlatformSDK
import tech.mobiledeveloper.jethabit.app.AppRes

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlatformSDK.init(PlatformConfiguration(activityContext = applicationContext,
            appName = AppRes.string.app_name))

        setContent {
            CompositionLocalProvider(
                LocalPlatform provides Platform.Android
            ) {
                App()   
            }
        }
    }
}