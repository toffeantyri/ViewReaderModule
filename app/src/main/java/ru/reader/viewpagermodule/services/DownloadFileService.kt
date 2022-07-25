package ru.reader.viewpagermodule.services

import android.app.Service
import android.content.Intent
import android.os.Binder

class DownloadFileService : Service() {

    private val iBinder: LocalBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getSevice(): DownloadFileService {
            return this@DownloadFileService
        }
    }

    override fun onBind(intent: Intent?): LocalBinder? {
        return iBinder
    }
}