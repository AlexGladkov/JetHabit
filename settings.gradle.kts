pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.android")) {
                useModule("com.android.tools.build:gradle:7.2.0")
            }

            if (requested.id.id.startsWith("com.squareup.sqldelight")) {
                useModule("com.squareup.sqldelight:gradle-plugin:1.5.4")
            }
        }
    }

    plugins {
        val kotlinVersion = extra["kotlin.version"] as String
        kotlin("multiplatform").version(kotlinVersion)

        val composeVersion = extra["compose.version"] as String
        id("org.jetbrains.compose").version(composeVersion)
    }
}

rootProject.name = "JetpackComposeDemo"
