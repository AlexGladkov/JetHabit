plugins {
    id(libs.plugins.kotlin.get().pluginId).version(libs.plugins.kotlin.get().version.requiredVersion).apply(false)
    id(libs.plugins.android.get().pluginId).version(libs.plugins.android.get().version.requiredVersion).apply(false)
    id(libs.plugins.compose.get().pluginId).version(libs.plugins.compose.get().version.requiredVersion).apply(false)
    id(libs.plugins.libres.get().pluginId).version(libs.plugins.libres.get().version.requiredVersion).apply(false)
}
