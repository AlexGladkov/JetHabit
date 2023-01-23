//fun main() {
//    val args = emptyArray<String>()
//    memScoped {
//        val argc = args.size + 1
//        val argv = (arrayOf("skikoApp") + args).map { it.cstr.ptr }.toCValues()
//        autoreleasepool {
//            UIApplicationMain(argc, argv, null, NSStringFromClass(SkikoAppDelegate))
//        }
//    }
//}
//
//class SkikoAppDelegate : UIResponder, UIApplicationDelegateProtocol {
//    companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta
//
//    @ObjCObjectBase.OverrideInit
//    constructor() : super()
//
//    private var _window: UIWindow? = null
//    override fun window() = _window
//    override fun setWindow(window: UIWindow?) {
//        _window = window
//    }
//
//    override fun application(application: UIApplication, didFinishLaunchingWithOptions: Map<Any?, *>?): Boolean {
//        window = UIWindow(frame = UIScreen.mainScreen.bounds)
//        window!!.rootViewController = Application("JetHabit") {
////            PlatformSDK.init(PlatformConfiguration())
////            val settingsEventBus = remember { SettingsEventBus() }
////            val currentSettings = settingsEventBus.currentSettings.collectAsState().value
////
////            MainTheme(
////                style = currentSettings.style,
////                darkTheme = currentSettings.isDarkMode,
////                corners = currentSettings.cornerStyle,
////                textSize = currentSettings.textSize,
////                paddingSize = currentSettings.paddingSize
////            ) {
////                val configuration = OdysseyConfiguration(
////                    backgroundColor = JetHabitTheme.colors.primaryBackground
////                )
////
////                CompositionLocalProvider(
////                    LocalSettingsEventBus provides settingsEventBus
////                ) {
////                    Column {
////                        Box(modifier = Modifier.fillMaxWidth().height(30.dp).background(JetHabitTheme.colors.primaryBackground))
////                        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
////                            setNavigationContent(configuration) {
////                                navigationGraph()
////                            }
////                        }
////                        Box(modifier = Modifier.fillMaxWidth().height(20.dp).background(JetHabitTheme.colors.primaryBackground))
////                    }
////                }
////            }
//        }
//        window!!.backgroundColor = UIColor.whiteColor
//        window!!.makeKeyAndVisible()
//        return true
//    }
//}
