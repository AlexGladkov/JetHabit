import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Application
import data.features.settings.LocalSettingsEventBus
import data.features.settings.SettingsEventBus
import di.PlatformConfiguration
import di.PlatformSDK
import kotlinx.cinterop.ObjCObjectBase
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCValues
import navigation.navigationGraph
import platform.Foundation.NSStringFromClass
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDelegateProtocol
import platform.UIKit.UIApplicationDelegateProtocolMeta
import platform.UIKit.UIApplicationMain
import platform.UIKit.UIResponder
import platform.UIKit.UIResponderMeta
import platform.UIKit.UIScreen
import platform.UIKit.UIWindow
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabitTheme
import ru.alexgladkov.odyssey.compose.setup.OdysseyConfiguration
import ru.alexgladkov.odyssey.compose.setup.setNavigationContent
import ui.themes.MainTheme

fun main() {
    val args = emptyArray<String>()
    memScoped {
        val argc = args.size + 1
        val argv = (arrayOf("skikoApp") + args).map { it.cstr.ptr }.toCValues()
        autoreleasepool {
            UIApplicationMain(argc, argv, null, NSStringFromClass(SkikoAppDelegate))
        }
    }
}

class SkikoAppDelegate : UIResponder, UIApplicationDelegateProtocol {
    companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta

    @ObjCObjectBase.OverrideInit
    constructor() : super()

    private var _window: UIWindow? = null
    override fun window() = _window
    override fun setWindow(window: UIWindow?) {
        _window = window
    }

    override fun application(application: UIApplication, didFinishLaunchingWithOptions: Map<Any?, *>?): Boolean {
        PlatformSDK.init(PlatformConfiguration())
        window = UIWindow(frame = UIScreen.mainScreen.bounds)
        window!!.rootViewController = Application("JetHabit") {
            val settingsEventBus = remember { SettingsEventBus() }
            val currentSettings = settingsEventBus.currentSettings.collectAsState().value

            Box(modifier = Modifier.fillMaxSize().background(Color.LightGray)) {
                Text(modifier = Modifier.align(Alignment.Center), text = "Bounds ${UIScreen.mainScreen.bounds}")
            }

//            MainTheme(
//                style = currentSettings.style,
//                darkTheme = currentSettings.isDarkMode,
//                corners = currentSettings.cornerStyle,
//                textSize = currentSettings.textSize,
//                paddingSize = currentSettings.paddingSize
//            ) {
//                val configuration = OdysseyConfiguration(
//                    backgroundColor = JetHabitTheme.colors.primaryBackground
//                )
//
//                CompositionLocalProvider(
//                    LocalSettingsEventBus provides settingsEventBus
//                ) {
//                    setNavigationContent(configuration) {
//                        navigationGraph()
//                    }
//                }
//            }
        }
        window!!.makeKeyAndVisible()
        return true
    }
}