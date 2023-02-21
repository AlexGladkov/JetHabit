package di

import org.kodein.di.DirectDI
import org.kodein.di.instance

object Inject {

    private var _di: DirectDI? = null

    val di: DirectDI
        get() = requireNotNull(_di)

    fun createDependencies(tree: DirectDI) {
        _di = tree
    }

    inline fun <reified T> instance(): T {
        return di.instance()
    }
}