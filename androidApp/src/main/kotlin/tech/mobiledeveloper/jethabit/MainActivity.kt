package tech.mobiledeveloper.jethabit

import MainView
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import di.PlatformConfiguration
import di.PlatformSDK

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlatformSDK.init(PlatformConfiguration(activityContext = applicationContext,
            appName = getString(R.string.app_name)))

        setContent {
            MainView(this)
        }
    }
}