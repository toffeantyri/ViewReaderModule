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
    }

    companion object {

        val getDirCache: File by lazy { initCache() }

        val getDirDownloads: File by lazy { initDirDownload()}

        val getDireDocuments: File by lazy { initDirDocument() }

        val getAssetManager : AssetManager by lazy { initAssetManager() }

        @Synchronized
        private fun initCache() = APP_CONTEXT.cacheDir

        @Synchronized
        private fun initDirDownload() =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        @Synchronized
        private fun initDirDocument() =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

        @Synchronized
        private fun initAssetManager() = APP_CONTEXT.assets
    }


    override fun onCreate() {
        super.onCreate()

    }

}