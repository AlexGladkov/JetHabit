package tech.mobiledeveloper.jethabit.app

import android.app.Application
import core.database.AppDatabase
import core.database.getDatabaseBuilder
import core.di.initializeCoil
import data.features.settings.SettingsEventBus

class JetHabitApp : Application() {
    val database: AppDatabase by lazy { getDatabaseBuilder(this).build() }
    val settingsEventBus: SettingsEventBus by lazy { SettingsEventBus() }

    override fun onCreate() {
        super.onCreate()
        initializeCoil(this)
    }
} 