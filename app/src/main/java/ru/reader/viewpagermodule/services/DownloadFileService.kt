package ru.reader.viewpagermodule.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.util.Log
import ru.reader.viewpagermodule.data.busines.storage.BookListHelper

class DownloadFileService : Service() {

    private val iBinder: LocalBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): DownloadFileService {
            return this@DownloadFileService
        }
    }

    override fun onBind(intent: Intent?): LocalBinder? {
        return iBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyLog", "LOADSERVICE onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val urlList = intent?.getStringArrayListExtra(BookListHelper.LIST_OF_URL_FOR_DOWNLOAD)
        Log.d("MyLog", "LOADSERVICE onStartCommand")
        urlList?.forEach { Log.d("MyLog", "LOADSERVICE onStartCommand $it")}

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyLog", "LOADSERVICE onDestroy")
    }
}