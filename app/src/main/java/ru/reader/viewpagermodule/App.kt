package ru.reader.viewpagermodule

import android.app.Application
import android.content.res.AssetManager
import android.os.Environment
import android.util.Log
import java.io.File


lateinit var APP_CONTEXT: Application

class App : Application() {

    init {
        APP_CONTEXT = this
        Log.d("MyLog", "init app")
    }

    companion object {


        val getDirCache: File by lazy { APP_CONTEXT.cacheDir }

        val getDirDownloads: File by lazy { Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) }

        val getDireDocuments: File by lazy { Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) }

        val getAssetManager : AssetManager by lazy { APP_CONTEXT.assets }

    }


    override fun onCreate() {
        super.onCreate()
        Log.d("MyLog", "onCreate App call")

    }

}