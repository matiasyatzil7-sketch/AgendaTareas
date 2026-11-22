package com.example.proyecto

import android.app.Application

class ProyectoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeManager.apply(this)
    }
}
