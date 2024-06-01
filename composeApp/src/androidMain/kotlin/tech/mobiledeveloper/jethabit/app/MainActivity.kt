package tech.mobiledeveloper.jethabit.app

import App
import MainView
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import core.database.getDatabaseBuilder
import di.LocalPlatform
import di.Platform
import di.PlatformConfiguration
import di.PlatformSDK
import org.jetbrains.compose.resources.stringResource
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.app_name

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appDatabase = getDatabaseBuilder(applicationContext).build()

        setContent {
            val appName: String = stringResource(Res.string.app_name)

            CompositionLocalProvider(
                LocalPlatform provides Platform.Android
            ) {
                App()
            }

            LaunchedEffect(Unit) {
                PlatformSDK.init(
                    configuration = PlatformConfiguration(
                        activityContext = applicationContext,
                        appName = appName
                    ),
                    appDatabase = appDatabase
                )
            }
        }
    }
}