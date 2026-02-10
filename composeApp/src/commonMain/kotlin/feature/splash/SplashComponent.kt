package feature.splash

import com.arkivanov.decompose.ComponentContext
import org.kodein.di.DI
import org.kodein.di.DIAware

class SplashComponent(
    componentContext: ComponentContext,
    override val di: DI,
    val onFinished: () -> Unit
) : ComponentContext by componentContext, DIAware
