package tech.mobiledeveloper.jethabit.app

import android.app.Application
import core.di.initializeCoil

class JetHabitApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeCoil(this)
    }
} 