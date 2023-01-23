
object Dependencies {

    object Compose {
        const val version = "1.4.0-alpha02"
        const val ui = "androidx.compose.ui:ui:$version"
        const val material = "androidx.compose.material:material:$version"
        const val tooling = "androidx.compose.ui:ui-tooling-preview:$version"
        const val livedata = "androidx.compose.runtime:runtime-livedata:$version"

        const val toolingTest = "androidx.compose.ui:ui-tooling:$version"
        const val uiTest = "androidx.compose.ui:ui-test-junit4:$version"

        const val accompanist = "com.google.accompanist:accompanist-systemuicontroller:0.18.0"
    }

    object LibRes {
        const val compose = "com.adeo.libres:libres-compose:1.1.2"
    }

    object Kotlin {
        const val version = "1.7.10"
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1"
        const val serializationPlugin = "org.jetbrains.kotlin:kotlin-serialization:$version"
        const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"
    }

    object Hilt {
        const val version = "2.40.5"
        const val android = "com.google.dagger:hilt-android:$version"
        const val navigation = "androidx.hilt:hilt-navigation-compose:1.0.0-rc01"
        const val compiler = "com.google.dagger:hilt-compiler:$version"
    }

    object Database {
        object Room {
            private const val version = "2.4.3"
            const val ktx = "androidx.room:room-ktx:$version"
            const val runtime = "androidx.room:room-runtime:$version"
            const val paging = "androidx.room:room-paging:2.4.0-alpha04"
            const val compiler = "androidx.room:room-compiler:$version"
        }

        object SqlDelight {
            private const val version = "1.5.4"
            const val desktop = "com.squareup.sqldelight:sqlite-driver:$version"
            const val android = "com.squareup.sqldelight:android-driver:$version"
            const val js = "com.squareup.sqldelight:sqljs-driver:$version"
            const val native = "com.squareup.sqldelight:native-driver:$version"
        }
    }

    object Navigation {
        const val navigationCompose = "androidx.navigation:navigation-compose:2.4.0-alpha10"

        object Odyssey {
            const val version = "1.3.3"
            const val core = "io.github.alexgladkov:odyssey-core:$version"
            const val compose = "io.github.alexgladkov:odyssey-compose:$version"
        }
    }

    object Arch {
        const val kodein = "org.kodein.di:kodein-di:7.17.0"

        object KViewModel {
            const val kviewmodel = "0.13"
            const val core = "com.adeo:kviewmodel:$kviewmodel"
            const val compose = "com.adeo:kviewmodel-compose:$kviewmodel"
            const val odyssey = "com.adeo:kviewmodel-odyssey:$kviewmodel"
        }
    }

    object Android {
        const val coreKtx = "androidx.core:core-ktx:1.6.0"
        const val appCompat = "androidx.appcompat:appcompat:1.3.1"
        const val material = "com.google.android.material:material:1.4.0"
    }

    object Lifecycle {
        const val lifecycleKtx = "androidx.lifecycle:lifecycle-runtime-ktx:2.3.1"
        const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07"
        const val activityCompose = "androidx.activity:activity-compose:1.3.1"
    }

    object Test {
        const val jUnit = "junit:junit:4.+"
        const val androidJUnit = "androidx.test.ext:junit:1.1.2"
        const val espresso = "androidx.test.espresso:espresso-core:3.3.0"
    }
}