import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id(libs.plugins.android.get().pluginId)
    id(libs.plugins.kotlin.get().pluginId)
    id(libs.plugins.compose.get().pluginId)
    id(libs.plugins.cocoapods.get().pluginId)
    id(libs.plugins.libres.get().pluginId)
    id(libs.plugins.sqldelight.get().pluginId).version(libs.plugins.sqldelight.get().version.requiredVersion)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

version = "1.0"

room {
    schemaDirectory("$projectDir/schemas")
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/data/schema"))
            migrationOutputDirectory.set(file("src/commonMain/sqldelight/migrations"))
        }
    }
}

libres {
    generatedClassName = "AppRes"
    generateNamedArguments = true
    baseLocaleLanguageCode = "en"
}

kotlin {
    jvmToolchain(17)
    androidTarget()
    jvm()

//    For now Room doesn't work with JS
//    js {
//        moduleName = "composeApp"
//        browser {
//            commonWebpackConfig {
//                outputFileName = "composeApp.js"
//            }
//        }
//        binaries.executable()
//    }

    listOf(
        iosArm64(),
        iosX64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = false
            linkerOpts.add("-lsqlite3")
        }
    }

    cocoapods {
        summary = "PlayZone iOS SDK"
        homepage = "https://google.com"
        ios.deploymentTarget = "14.0"

        framework {
            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            transitiveExport = false
            baseName = "SharedSDK"
        }
    }

    targets.withType<KotlinNativeTarget> {
        binaries {
            all {
                linkerOpts("-lsqlite3")
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.datetime)

            implementation(libs.klock.common)
            implementation(libs.libres.compose)
            implementation(libs.kodein.di)

            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.compose.navigation)
            implementation(libs.compose.viewmodel)

            implementation(libs.room.runtime)
            implementation(libs.room.sqlite)
            implementation(libs.room.sqlite.bundled)

        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }

        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activity.compose)
            implementation(libs.sqldelight.android.driver)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.common)
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.klock.jvm)
        }

        jsMain.dependencies {
            implementation(compose.html.core)
            implementation(libs.sqldelight.js.driver)
            implementation(npm("sql.js", "1.6.2"))
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.0.1"))
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.room.compiler)
    add("kspAndroid", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspJvm", libs.room.compiler)
}

android {
    namespace = "tech.mobiledeveloper.jethabit.app"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.mobiledeveloper.playzone_mobile.android"
        minSdk = libs.versions.mindsdk.get().toInt()
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

compose.desktop {
    application {
        mainClass = "Main_desktopKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "tech.mobiledeveloper.jethabit.desktopApp"
            packageVersion = "1.0.0"
        }
    }
}

compose.experimental {
    web.application {}
}