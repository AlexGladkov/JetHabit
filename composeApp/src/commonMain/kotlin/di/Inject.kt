package di

object Inject {
    inline fun <reified T> instance(): T = PlatformSDK.instance()
}