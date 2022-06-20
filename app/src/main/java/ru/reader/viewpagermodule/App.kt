package ru.reader.viewpagermodule

import android.app.Application


lateinit var APP_CONTEXT : Application
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        APP_CONTEXT = this
    }

}