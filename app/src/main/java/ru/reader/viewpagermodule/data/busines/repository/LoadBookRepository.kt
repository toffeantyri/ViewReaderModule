package ru.reader.viewpagermodule.data.busines.repository

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.reader.viewpagermodule.APP_CONTEXT
import ru.reader.viewpagermodule.data.api.ApiProvider
import ru.reader.viewpagermodule.data.busines.storage.BookListHelper
import ru.reader.viewpagermodule.services.DownloadFileService

class LoadBookRepository(val api: ApiProvider) : BaseRepository<LoadingBookState>() {

    private val context = APP_CONTEXT
    private var loadService: Service? = null
    private var serviceBound = false

    suspend fun loadBook(listUrl: ArrayList<String>, onSuccess: () -> Unit, onFail: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val loadIntent = Intent(context, DownloadFileService::class.java)
            loadIntent.putStringArrayListExtra(BookListHelper.LIST_OF_URL_FOR_DOWNLOAD, listUrl)
            context.startForegroundService(loadIntent)
            context.bindService(loadIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            onSuccess()
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as DownloadFileService.LocalBinder
            loadService = binder.getService()
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
            loadService = null
        }
    }
}
