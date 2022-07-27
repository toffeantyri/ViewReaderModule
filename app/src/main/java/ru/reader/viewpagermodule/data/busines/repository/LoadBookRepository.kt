package ru.reader.viewpagermodule.data.busines.repository

import android.app.Service
import android.content.*
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.reader.viewpagermodule.APP_CONTEXT
import ru.reader.viewpagermodule.data.busines.storage.BookListHelper
import ru.reader.viewpagermodule.services.DownloadFileService
import ru.reader.viewpagermodule.services.DownloadFileServiceBroadcastReceiver

const val BROADCAST_SERVICE_LOAD_STATE = "BROADCAST_SERVICE_LOAD_STATE"
const val TAG_NEW_DOWNLOAD_SERVICE_STATE = "TAG_NEW_DOWNLOAD_SERVICE_STATE "

class LoadBookRepository() : BaseRepository<LoadingBookState>() {

    private val context = APP_CONTEXT
    private var loadService: DownloadFileService? = null
    private var serviceBound = false
    private var serviceState: LoadingBookState = LoadingBookState.IDLE_LOAD
        set(value) {
            field = value
            Log.d("MyLog", value.toString())
        }

    suspend fun loadBook(listUrl: ArrayList<String>, onSuccess: () -> Unit, onFail: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            registerBroadcastLoadService()
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
            loadService!!.tellMeCurrentState()
            Log.d("MyLog", "ServiceBound")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
            loadService = null
            Log.d("MyLog", "ServiceUnbound")
        }
    }

    private fun registerBroadcastLoadService() {
        context.registerReceiver(broadcastReceiverNewServiceState, IntentFilter(BROADCAST_SERVICE_LOAD_STATE))
    }

    private val broadcastReceiverNewServiceState = object : DownloadFileServiceBroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("MyLog", "onReceive")
            serviceState = intent?.getSerializableExtra(TAG_NEW_DOWNLOAD_SERVICE_STATE) as LoadingBookState
            when (serviceState) {
                LoadingBookState.SUCCESS_LOAD -> {
                    loadService?.stopForeground(true)
                    loadService?.stopSelf()
                }
                LoadingBookState.LOAD_FAIL -> {
                }
                LoadingBookState.IDLE_LOAD -> {
                }
                LoadingBookState.LOADING -> {
                }
            }
        }
    }
}


